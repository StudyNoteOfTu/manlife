package com.example.tufengyi.manlife.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tufengyi.manlife.utils.PrefParams;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this,"wx7ef876fe1742f5df",false);
        api.handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent,this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq){}

    @Override
    public void onResp(BaseResp baseResp){
        String result = "";
        switch(baseResp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp)baseResp).code;
                Log.d("TestWX","code"+code);
                SharedPreferences WxSp = getApplication().getSharedPreferences(PrefParams.spName, Context.MODE_PRIVATE);
                SharedPreferences.Editor WxSpEditor = WxSp.edit();
                WxSpEditor.putString(PrefParams.CODE,code);
                WxSpEditor.apply();

                SharedPreferences sp_code = getApplication().getSharedPreferences("code",0);
                SharedPreferences.Editor mEditor = sp_code.edit();
                mEditor.putString("code",code);
                mEditor.apply();


                Intent intent = new Intent();
                intent.setAction("authlogin");


                WXEntryActivity.this.sendBroadcast(intent);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result="发送取消";
                Toast.makeText(this,result,Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result="发送被拒绝";
                Toast.makeText(this,result,Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                String codetest = ((SendAuth.Resp)baseResp).code;
                Log.d("TestWX","code"+codetest);
                result = "发送返回";
                Toast.makeText(this,result,Toast.LENGTH_LONG).show();
                finish();
                break;

        }
    }

}
