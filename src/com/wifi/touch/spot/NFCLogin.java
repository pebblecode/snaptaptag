package com.wifi.touch.spot;

//import java.util.Iterator;
import java.util.List;

//import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
//import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
//import android.os.Bundle;
import android.util.Log;
//import android.widget.ArrayAdapter;

public class NFCLogin  {

	//saveWepConfig();
	//readWepConfig();

	private String mnetworkSSID = "";
	private String mnetworkPass = "";
	private String mnetworkType = "WPA";
	boolean malreadyconnectedtodifferentnetwork = false;
	boolean malreadyconnectedtorightnetwork = false;


	public NFCLogin(String networkSSID, String networkPass, String networkType)
	{
		mnetworkSSID = networkSSID;
		mnetworkPass = networkPass;
		mnetworkType = networkType;
	}



	// boolean setupNetworkConfig(networkSSID, networkPass, networkType);
	// }
	void readWepConfig(Context context)
	{ 
		WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> item = wifi.getConfiguredNetworks();
		int i = item.size();
		Log.i("WifiPreference", "NO OF CONFIG " + i );
		//Iterator<WifiConfiguration> iter =  item.iterator();
		WifiConfiguration config = item.get(5);
		Log.i("WifiPreference", "SSID" + config.SSID);
		Log.i("WifiPreference", "PASSWORD" + config.preSharedKey);
		Log.i("WifiPreference", "ALLOWED ALGORITHMS");
		Log.i("WifiPreference", "LEAP" + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
		Log.i("WifiPreference", "OPEN" + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
		Log.i("WifiPreference", "SHARED" + config.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED));
		Log.i("WifiPreference", "GROUP CIPHERS");
		Log.i("WifiPreference", "CCMP" + config.allowedGroupCiphers.get(GroupCipher.CCMP));
		Log.i("WifiPreference", "TKIP" + config.allowedGroupCiphers.get(GroupCipher.TKIP));
		Log.i("WifiPreference", "WEP104" + config.allowedGroupCiphers.get(GroupCipher.WEP104));
		Log.i("WifiPreference", "WEP40" + config.allowedGroupCiphers.get(GroupCipher.WEP40));
		Log.i("WifiPreference", "KEYMGMT");
		Log.i("WifiPreference", "IEEE8021X" + config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
		Log.i("WifiPreference", "NONE" + config.allowedKeyManagement.get(KeyMgmt.NONE));
		Log.i("WifiPreference", "WPA_EAP" + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
		Log.i("WifiPreference", "WPA_PSK" + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
		Log.i("WifiPreference", "PairWiseCipher");
		Log.i("WifiPreference", "CCMP" + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
		Log.i("WifiPreference", "NONE" + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
		Log.i("WifiPreference", "TKIP" + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));
		Log.i("WifiPreference", "Protocols");
		Log.i("WifiPreference", "RSN" + config.allowedProtocols.get(Protocol.RSN));
		Log.i("WifiPreference", "WPA" + config.allowedProtocols.get(Protocol.WPA));
		Log.i("WifiPreference", "WEP Key Strings");
		String[] wepKeys = config.wepKeys;
		Log.i("WifiPreference", "WEP KEY 0" + wepKeys[0]);
		Log.i("WifiPreference", "WEP KEY 1" + wepKeys[1]);
		Log.i("WifiPreference", "WEP KEY 2" + wepKeys[2]);
		Log.i("WifiPreference", "WEP KEY 3" + wepKeys[3]);
	}
	boolean setupNetworkConfig(Context context, ConnectivityManager connManager)
	{
		//WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration wc = new WifiConfiguration(); 

		wc.SSID = "\"" + mnetworkSSID + "\"";//"\"SSID_NAME\""; 
		//wc.hiddenSSID = true;
		wc.status = WifiConfiguration.Status.ENABLED;     
		//wc.priority = 40;
		boolean networkTypeValid = true;
		//wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		//wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
		//wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		//wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		//wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		//wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		//wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		//wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		//wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

		//wc.wepKeys[0] = "\"aftfnzlb\"";//"\"aaabbb1234\""; //This is the WEP Password
		//wc.wepTxKeyIndex = 0;
		if(mnetworkType.equals("WEP"))
		{
			wc.hiddenSSID = true;
		    wc.status = WifiConfiguration.Status.DISABLED;     
		    wc.priority = 40;
		    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		    wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
		    wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);


			wc.wepKeys[0] = "\"" + mnetworkPass + "\""; 
			wc.wepTxKeyIndex = 0;
			
			//wc.allowedKeyManagement.set(KeyMgmt.NONE);
			
			//wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			//wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			//wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			//wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			//wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		}
		else if(mnetworkType.equals("WPA"))
		{
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
	        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

			
			wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			wc.preSharedKey = "\""+ mnetworkPass +"\"";

		}
		else if(mnetworkType.equals("OPEN"))
		{
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		}
		else if(mnetworkType.equals("EAP"))
		{
			//TODO		
		}
		else
		{
			networkTypeValid = false;
		}

		if (networkTypeValid)
		{
			//WifiManager wifiManag = (WifiManager) this
			//		.getSystemService(WIFI_SERVICE);
			//ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			WifiManager wifiManag = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			
			if(wifiManag != null)
			{
				boolean res1 = wifiManag.setWifiEnabled(true);
			}
			boolean alreadyconnected = false;
			boolean alreadyconnectedtorightnetwork=false;
			if(mWifi.isConnected())
			{
				alreadyconnected = true;
				
				String SSID="";
				if(wifiManag != null)
				{
				  SSID = wifiManag.getConnectionInfo().getSSID();
				}
				if(mnetworkSSID.equals(SSID))
				{
				   malreadyconnectedtorightnetwork = true;
				}
			}
			
			if(!(alreadyconnected && malreadyconnectedtorightnetwork))
			
			{
				boolean foundnetworkinrange = false;
				List<ScanResult> ls = (List<ScanResult>) wifiManag.getScanResults();
				for(int i=0;i<ls.size();i++)
				{   
					Log.i("VALUE"," "+ls.get(i).toString());
					Log.i("",""+ls.get(i).SSID);
					if(ls.get(i).SSID.equalsIgnoreCase(mnetworkSSID))
					{
						Log.i("",mnetworkSSID + " found");
						foundnetworkinrange = true;
						break;
					}
				}
				if(foundnetworkinrange)
				{
					List<WifiConfiguration> list = wifiManag.getConfiguredNetworks();
					boolean networkAlreadyExists = false;
					int networkthatexists_id = 0;
					//WifiInfo wifiInfo = null;
					for (WifiConfiguration i : list) {
                         
						if (i.SSID != null && i.SSID.equals("\"" + mnetworkSSID + "\"")) {
							networkAlreadyExists = true;
							networkthatexists_id = i.networkId;
							break;
						}
					}
					if(networkAlreadyExists)
					{						
						wifiManag.removeNetwork(networkthatexists_id);						
					}
					
					int newnetworkid = wifi.addNetwork(wc);
					boolean b = wifi.enableNetwork(newnetworkid, true);
					Log.i("Wifi", "enableNetwork returned " + b);
					if(alreadyconnected && (!alreadyconnectedtorightnetwork))
					{
						malreadyconnectedtodifferentnetwork = true;
					}
				}
				else
				{
					Log.e("Error","Network out of range");
					return false;
				}
			} 	
			
			
			return true;
		} 
		else
		{
			Log.e("Error", "networkType Invalid");
			return false;
		}
	}

}