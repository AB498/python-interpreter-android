package org.ab498.videofied;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MainActivity extends AppCompatActivity {

    Python py;
    String code,interpreterOutput=".";
    PyObject console,io,sys, textOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPy();

        interpreterOutput=py(Filess.readFullAssets(this, "src/main/python/mod_video.py"));

        PyObject ppp=py.getModule("mod_video");
        //ppp.callAttr("main", null);

        EditText et = findViewById(R.id.et);
        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                TextView t = findViewById(R.id.t);
                t.setText(py(et.getText().toString()));
            }
        });


    }

    public String py(String code){
 String interpreterOutput="";
        try {
            console.callAttrThrows("mainTextCode", code);

            interpreterOutput = textOutputStream.callAttr("getvalue").toString();
        }catch (PyException e){
            // If there's an error, you can obtain its output as well
            // e.g. if you mispell the code
            // Missing parentheses in call to 'print'
            // Did you mean print("text")?
            // <string>, line 1
            interpreterOutput = e.getMessage().toString();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
return interpreterOutput;
    };
    public void initPy(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(getApplicationContext()));
        }

        py = Python.getInstance();
        sys = py.getModule("sys");
        io = py.getModule("io");
        console = py.getModule("interpreter");
        textOutputStream = io.callAttr("StringIO");
        sys.put("stdout", textOutputStream);
    }
}