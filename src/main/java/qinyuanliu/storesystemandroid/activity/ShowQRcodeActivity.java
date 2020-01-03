package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2018/9/17.
 */

public class ShowQRcodeActivity extends BaseActivity {
    private Button btn_refresh;
    private ImageView img_qrcode;
    private Button btn_back;
    private Handler timerhandler;
    private int maxtimeout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showqrcode);

        initView();
        StartQRcodeTimer();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndQRcodeTimer();
                finish();
            }
        });

        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshCodeAPI();
            }
        });
        img_qrcode = (ImageView) findViewById(R.id.img_qrcode);
    }


    Runnable timerrunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (maxtimeout % 60 == 0) {
                    Message message = new Message();
                    message.what = 1;
                    myHandler.sendMessage(message);

                }
                timerhandler.postDelayed(this, 1000);
                maxtimeout++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    RefreshCodeAPI();
                    break;

            }
        }
    };

    private void StartQRcodeTimer() {
        timerhandler = new Handler();
        maxtimeout = 0;
        timerhandler.post(timerrunnable);
    }

    private void EndQRcodeTimer() {
        if (timerhandler != null) {
            timerhandler.removeCallbacksAndMessages(null);
        }
    }

    private void RefreshCodeAPI() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetQRCode(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.QRcodeResult res = (SCResult.QRcodeResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.codevalue != null && !res.codevalue.equals("")) {
                                                Bitmap qrBitmap = generateBitBitmap(res.codevalue, 600, 600);
                                                img_qrcode.setImageBitmap(qrBitmap);
                                            } else {
                                                img_qrcode.setImageDrawable(getResources().getDrawable(R.drawable.icon_defaultimg));
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ShowQRcodeActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ShowQRcodeActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private Bitmap generateBitBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0xff000000;//black
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            Bitmap tempbitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
            return tempbitmap;
            //createCenterBitmap2(encode,tempbitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
