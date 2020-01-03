package qinyuanliu.storesystemandroid.util.screenlock;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;


/**
 * Created by qinyuanliu on 2019/3/4.
 */

public class PassDialog extends Dialog {
    private Context context;//上下文
    private EditText edt_lockcode;
    private Button btn_confirm;
    private String lockpwd;
    SharedPreferences sp;
    private String locallock;

    public PassDialog(Context context) {
        super(context, R.style.LockDialog);//加载dialog的样式
        this.context = context;

    }

    //禁止后退键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        setContentView(R.layout.lockdialog);

        sp = context.getSharedPreferences("storesystemdemo", Context.MODE_PRIVATE);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
       // lp.type = WindowManager.LayoutParams.TYPE_PHONE;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失
        //遍历控件id添加点击注册
        //  findViewById(id).setOnClickListener(this);
        edt_lockcode = (EditText) findViewById(R.id.edt_lockcode);
        edt_lockcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lockpwd = s.toString();
            }
        });
        edt_lockcode.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockpwd != null && !lockpwd.equals("")) {
                    locallock = sp.getString("lockCode", "8888");
                    if (lockpwd.equals(locallock)) {
                        edt_lockcode.setText("");
                        lockpwd = "";
                        Toast.makeText(context, "解锁成功！", Toast.LENGTH_SHORT).show();
                        Session.getInstance().setIsLocked(false);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                        dismiss();
                    } else {
                        edt_lockcode.setText("");
                        Toast.makeText(context, "锁屏密码错误！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "请输入锁屏密码！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
