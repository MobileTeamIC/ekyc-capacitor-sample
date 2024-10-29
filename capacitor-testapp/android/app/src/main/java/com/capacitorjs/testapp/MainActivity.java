package com.capacitorjs.testapp;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      registerPlugin(EkycPlugin.class);
      super.onCreate(savedInstanceState);
   }
}
