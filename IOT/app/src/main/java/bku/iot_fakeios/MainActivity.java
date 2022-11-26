package bku.iot_fakeios;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi, txtLux, txtAI;
    LabeledSwitch btnLED, btnLED2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        txtLux = findViewById(R.id.txtLuxury);
        txtAI = findViewById(R.id.txtAI);
        btnLED = findViewById(R.id.btnLED);
        btnLED2 = findViewById(R.id.btnLED2);

        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("EmChes/feeds/nutnhan1", "1");
                }else{
                    sendDataMQTT("EmChes/feeds/nutnhan1", "0");
                }
            }
        });

        btnLED2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("EmChes/feeds/nutnhan2", "3");
                }else{
                    sendDataMQTT("EmChes/feeds/nutnhan2", "2");
                }
            }
        });
        startMQTT();
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
//                btnLED.setVisibility(View.INVISIBLE);
//                btnLED2.setVisibility(View.INVISIBLE);
            }

            @Override
            public void connectionLost(Throwable cause) {
//                btnLED.setVisibility(View.INVISIBLE);
//                btnLED2.setVisibility(View.INVISIBLE);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" + message.toString());
                if(topic.contains("cambien1")){
                    txtTemp.setText(message.toString()+"\n°C");
                }else if(topic.contains("cambien2")){
                    txtHumi.setText(message.toString()+"\n%");
                }else if(topic.contains("cambien3")){
                    txtLux.setText(message.toString()+"\nLux");
                }else if(topic.contains("ai")){
                    txtAI.setText(message.toString());
                }else if(topic.contains("nutnhan1")){
                    if(message.toString().equals("1")){
                        btnLED.setOn(true);
                    }else{
                        btnLED.setOn(false);
                    }
                }else if(topic.contains("nutnhan2")){
                    if(message.toString().equals("3")){
                        btnLED2.setOn(true);
                    }else{
                        btnLED2.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}