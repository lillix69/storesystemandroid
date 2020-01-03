package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.util.zxing.camera.CameraManager;
import qinyuanliu.storesystemandroid.util.zxing.decoding.InactivityTimer;
import qinyuanliu.storesystemandroid.util.zxing.decoding.ScanPrepareHandler;
import qinyuanliu.storesystemandroid.util.zxing.view.ViewfinderView;

/**
 * Created by qinyuanliu on 2018/12/6.
 */

public class ScanPrepareActivity extends Activity implements SurfaceHolder.Callback {
    private ScanPrepareHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private SurfaceView surfaceView;
    private Button btn_back;

    private ArrayList<String> scanlist = new ArrayList<>();
    private ArrayList<String> modelcodelist;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_scanprepare);

modelcodelist = getIntent().getStringArrayListExtra("modelcodelist");

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        CameraManager.init(getApplication());

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

       resumeScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void resumeScan(){
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        String resultString = result.getText();
        onResultHandler(resultString, barcode);
    }

    private String scanstr;

    private void onResultHandler(String resultString, Bitmap bitmap) {
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(ScanPrepareActivity.this, "扫码失败!", Toast.LENGTH_SHORT).show();
            return;
        }
        //关闭扫描
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        try {
            String gkbstr = new String(resultString.getBytes("ISO-8859-1"), "GB2312");
            if(gkbstr.contains("?")){
                if(gkbstr.substring(0,1).equals("?")) {
                    scanstr = resultString.substring(1);
                }
                else{
                    scanstr = resultString;
                }
                Log.i("扫码utf======", scanstr);
            }
            else{
                scanstr = gkbstr;
                Log.i("扫码gkb======",scanstr);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //扫描结果加入list中
        if (!scanlist.contains(scanstr)) {
            scanlist.add(scanstr);
        }
        //判断是型号还是产品// 0:产品  1：型号  2：出入权限二维码
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            final String searchsn = scanstr;
                            SCSDK.getInstance().GetTypeByScan(Session.getInstance().getShopCode(), searchsn, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ScanResult product = (SCResult.ScanResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (product.result == 0) {
                                                SCSDK.getInstance().GetProductdetailByScan(Session.getInstance().getShopCode(), searchsn, Session.getInstance().getToken(), new SCResponseListener() {
                                                    @Override
                                                    public void onResult(Object result) {
                                                        final SCResult.ProductDetailResult product = (SCResult.ProductDetailResult) result;
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if(modelcodelist.contains(product.modelcode)) {
                                                                    if( Session.getInstance().getScansnsMap().get(product.modelcode) == null){
                                                                        HashSet<String> snsSet = new HashSet<String>();
                                                                        snsSet.add(searchsn);
                                                                        Session.getInstance().getScansnsMap().put(product.modelcode,snsSet);
                                                                    }
                                                                    else {
                                                                        Session.getInstance().getScansnsMap().get(product.modelcode).add(searchsn);
                                                                    }
                                                                    Show(searchsn, product.modelname);
                                                                }
                                                                else{
                                                                    Toast.makeText(ScanPrepareActivity.this,"扫描到条码【" + searchsn + "】不在出库明细里！", Toast.LENGTH_SHORT).show();
                                                                    resumeScan();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(final int code, final String errormsg) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                if(code == 404) {
                                                                    Intent intent = new Intent(ScanPrepareActivity.this, ScanErrorActivity.class);
                                                                    intent.putExtra("ScanString", searchsn);
                                                                    intent.putExtra("ErrorString",errormsg);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
else{
                                                                    Toast.makeText(ScanPrepareActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(ScanPrepareActivity.this, "扫描到条码【" + searchsn + "】不是产品条码！", Toast.LENGTH_SHORT).show();
                                                resumeScan();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(final int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(code == 404){
                                                Toast.makeText(ScanPrepareActivity.this, "扫描到条码【" + searchsn + "】不是产品条码！", Toast.LENGTH_SHORT).show();
                                                resumeScan();
                                            }else {
                                                Toast.makeText(ScanPrepareActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });
                        } else {
                            Toast.makeText(ScanPrepareActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }

    private void Show(String sn, String modelname) {
        //弹窗提示是否继续扫描
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanPrepareActivity.this);
        builder.setTitle("提示");
        builder.setMessage("扫描到条码【" + sn + "】" + modelname + ",是否继续进行扫码出库?");
        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resumeScan();
            }
        });
        builder.setNegativeButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_OK);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new ScanPrepareHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }
}
