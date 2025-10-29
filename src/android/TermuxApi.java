package com.example.termuxapi;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;

public class TermuxApi extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("getSimInfo".equals(action)) {
            getSimInfo(callbackContext);
            return true;
        } else if ("sendSMS".equals(action)) {
            String number = args.getString(0);
            String message = args.getString(1);
            int simIndex = args.getInt(2);
            sendSMS(number, message, simIndex, callbackContext);
            return true;
        }
        return false;
    }

    private void getSimInfo(CallbackContext callbackContext) {
        TelephonyManager tm = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        JSONArray result = new JSONArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            int count = tm.getPhoneCount();
            try {
                for (int i = 0; i < count; i++) {
                    String carrier = tm.getSimOperatorName(i);
                    org.json.JSONObject sim = new org.json.JSONObject();
                    sim.put("carrierName", carrier);
                    result.put(sim);
                }
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
                return;
            }
        }
        callbackContext.success(result);
    }

    private void sendSMS(String number, String message, int simIndex, CallbackContext callbackContext) {
        try {
            SmsManager smsManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                smsManager = SmsManager.getSmsManagerForSubscriptionId(simIndex);
            } else {
                smsManager = SmsManager.getDefault();
            }
            smsManager.sendTextMessage(number, null, message, null, null);
            callbackContext.success("SMS enviado");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }
}
