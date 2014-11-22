package com.wifi.touch.spot;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.URL;


import android.app.Activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import android.nfc.tech.Ndef;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.WindowManager;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Toast;

import com.wifi.touch.spot.SimpleCrypto;


import com.wifi.touch.spot.NFCLogin;
import android.nfc.tech.NdefFormatable;

public class WifitouchspotActivity extends Activity {
	/** Called when the activity is first created. */
	
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;
	private String[][] techListsArray;
	
	private String mLandingPath;

	private String mnetworkSSID;
	private String mnetworkPass;
	private String mnetworkType;

	//private final Map<String, Drawable> drawableMap;
	private WebView mWeb;
	ProgressDialog mProgress;
	boolean mcouldnotconnectflag =false;
	Context mcontext = null;
	private boolean malreadyconnectedtodifferentnetwork = false;
	private boolean malreadyconnectedtorightnetwork = false;

	static private final String[] PREFIXES={"http://www.", "https://www.",
		"http://", "https://",
		"tel:", "mailto:",
		"ftp://anonymous:anonymous@",
		"ftp://ftp.", "ftps://",
		"sftp://", "smb://",
		"nfs://", "ftp://",
		"dav://", "news:",
		"telnet://", "imap:",
		"rtsp://", "urn:",
		"pop:", "sip:", "sips:",
		"tftp:", "btspp://",
		"btl2cap://", "btgoep://",
		"tcpobex://",
		"irdaobex://",
		"file://", "urn:epc:id:",
		"urn:epc:tag:",
		"urn:epc:pat:",
		"urn:epc:raw:",
		"urn:epc:", "urn:nfc:"};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setUpForegroundDispatchSystem();

		mWeb = (WebView) findViewById(R.id.webView1);

		final Activity activity = this;
		setWebViewClient(activity);

		mWeb.getSettings().setJavaScriptEnabled(true);
		//String summary = "<html><body><div align=\"center\" style=\"margin-top: 100px\" ><img src=\"file:///android_asset/BB.png\" width=\"100px\"/></div><div align=\"center\" style=\"margin-top:120px;\"><h2 style=\"color:#3fc8f4\">Please scan a<br/>wifi touch point</h2></div></body></html>";
		String summary = "<html><body><div align=\"center\" style=\"margin-top: 100px\" ></div><div align=\"center\" style=\"margin-top:120px;\"><h2 style=\"color:#3fc8f4\">Please scan a<br/>wifi touch point</h2></div></body></html>";
		mWeb.loadDataWithBaseURL("",summary, "text/html", "UTF-8", "");
		
		mainProgram(getIntent());
	}

	private void mainProgram(Intent intent)
	{
		

		String action = intent.getAction();
		if (   NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)   ||
				NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) 
		{
			Tag myTag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            
          
			//WriteTagData(myTag);

			if(ReadTagData(intent))  // ReadTagData() sets up the member vars mnetworkSSID, mnetworkPass, mnetworkType
				                     //                                  and mLandingPath
			{
				NFCLogin mylogin = new NFCLogin(mnetworkSSID, mnetworkPass, mnetworkType);
				ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				if(mylogin.setupNetworkConfig(getBaseContext(), connManager))  
				{
					malreadyconnectedtodifferentnetwork = mylogin.malreadyconnectedtodifferentnetwork;
					malreadyconnectedtorightnetwork = mylogin.malreadyconnectedtorightnetwork;
					
					MediaPlayer mp = MediaPlayer.create(WifitouchspotActivity.this, R.raw.discovered_tag_notification);   
					mp.start();
					if(!malreadyconnectedtorightnetwork)
					{
					  loadLandingPage(this);
					}
					else
					{
						 mWeb.loadUrl("http://test.tait.io/");// + mLandingPath + "/");
					}
				}
				else
				{
					Toast.makeText(WifitouchspotActivity.this, "Sorry, couldn't connect, perhaps the network is out of range? Please try again", Toast.LENGTH_LONG).show();
				}
			}	
		}
	}

	private void WriteTagData(Tag myTag) {
		// TODO Auto-generated method stub
		NdefRecord wifirecord = null;
		try {
			wifirecord = this.createRecord();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] url=buildUrlBytes();
		NdefRecord urlrecord=new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_URI,
				new byte[] {}, url);
		//NdefMessage msg=new NdefMessage(new NdefRecord[] {urlrecord});


		NdefMessage myndefmessage = new NdefMessage(new NdefRecord[] { urlrecord, wifirecord });

		writeTag(myndefmessage, myTag);
	}

	private void setWebViewClient(final Activity activity) {
		// TODO Auto-generated method stub
		mWeb.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {              
				view.loadUrl(url);
				return true;
			}
			public void onLoadResource (WebView view, String url) {
			
			}
			public void onPageFinished(WebView view, String url) {
				if(mProgress!=null)
				{
				 if (mProgress.isShowing()) {
				  	  mProgress.dismiss();
					  mProgress = null;
				  }
				}
			}
			public void onReceivedError(WebView view, int errorCode, String
					description, String failingUrl) {
				//Toast.makeText(activity, "Internet connection down? " +
				//description, Toast.LENGTH_LONG).show();

				//view.loadUrl("http://www.geanest.com/businessportfolio/download/" + mLandingPath + "/"); 
				//view.loadData("<div align=\"center\" ><h2>Error connecting to wireless, you could try tapping below in a few moments</h2></div>" + 
				//		"<div align=\"center\"><a href=\""+failingUrl +"\">Tap Here</a><br/><h2>or press back and rescan tag</h2></div>", "text/html", "UTF-8");
				view.loadData("<div align=\"center\" style=\"margin-top: 140px;color:#3fc8f4;\"><h2>Sorry, error connecting to WiFi... </h2><br/><h2>Please rescan tag</h2></div>", "text/html", "UTF-8"); 
			} 
		});
	}

	private boolean ReadTagData(Intent intent)
	{
		String wifiInfo = null;
		NdefMessage[] messages = NfcUtils.getNdefMessages(intent);
		if(messages[0].getRecords().length != 2)
		{
			return false;
		}
		else
		{
			//for(int j=0; j<messages[0].getRecords().length; j++)
			//{
			byte[] payload = messages[0].getRecords()[1].getPayload();
			wifiInfo = new String(payload);
			Log.i("Comment", wifiInfo);
			//}

			String mydata = "";

			String[] RowData2 = wifiInfo.split(",");
			// have a look, parse the Strings for network type etc...
			if(RowData2.length ==4)
			{        
				String encrypted =  RowData2[0];
				try {
					mydata = SimpleCrypto.decrypt("password", encrypted);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mnetworkSSID = RowData2[1];//"Frank's Bar Web";
				mnetworkPass = mydata;//"0AF3C04C0F";
				mnetworkType = RowData2[2];//"WPA";
				mLandingPath = RowData2[3];	
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	private void loadLandingPage(final Activity activity) {
		final boolean alreadyconnectedtodifferentnetwork = malreadyconnectedtodifferentnetwork;
		//mProgress = null;
		//if (mProgress == null) {
		//	mProgress = new ProgressDialog((Context) activity);//(Context)WifitouchspotActivity.this);
		//	mProgress.setMessage("Please wait, attempting to connect to wifi...");
		//	WindowManager.LayoutParams WMLP = mProgress.getWindow().getAttributes();
		//	WMLP.y = 100;   //y position

		//	mProgress.getWindow().setAttributes(WMLP);
		//	mProgress.show();

			

		//}
		
		String summary = "<html><body><div align=\"center\" style=\"margin-top: 140px\" ><img src=\"file:///android_asset/load.gif\" width=\"100px\"/></div><div align=\"center\" style=\"margin-top:100px;\"><h2 style=\"color:#3fc8f4\">Please wait<br/>Connecting to WiFi</h2></div></body></html>";
		mWeb.loadDataWithBaseURL("",summary, "text/html", "UTF-8", ""); 
		
		
		
		
		mcontext = WifitouchspotActivity.this;


		final Handler mHandler = new Handler();
		final Runnable mUpdateResults = new Runnable() {
			public void run() {
				//Toast.makeText(mcontext, "Sorry, couldn't connect perhaps network out of range, please try again", Toast.LENGTH_LONG).show();
				mWeb.loadData("<div align=\"center\" style=\"margin-top: 140px;color:#3fc8f4;\"><h2>Sorry, error connecting to WiFi... </h2><br/><h2>Please rescan tag</h2></div>", "text/html", "UTF-8"); 
			}
		};

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					int count=0;

					ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
					NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					WifiManager wifiManag = (WifiManager)mcontext.getSystemService(Context.WIFI_SERVICE);
					String SSID = null;
					if(wifiManag != null)
					{
					  SSID = wifiManag.getConnectionInfo().getSSID(); //&& (!mnetworkSSID.equals(SSID)
					}
					int maxcount = 14;
					if(alreadyconnectedtodifferentnetwork)
					{
				       
					  //Thread.sleep(3000);
					  
					}
					while ( ((SSID == null) || (!mWifi.isConnected()))&& (count<maxcount) ){  //((!mWifi.isConnected() && (!mnetworkSSID.equals(SSID))  )) && (count < maxcount)) {
						Thread.sleep(2000);
						mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						wifiManag = (WifiManager)mcontext.getSystemService(Context.WIFI_SERVICE);
						if(wifiManag != null)
						{
							SSID = wifiManag.getConnectionInfo().getSSID();
						}
						
						//times out after maxcount seconds
						count++;   
					}
					Log.i("out of loop with", "SSID ="+SSID + " connected= " + mWifi.isConnected());

					if(count ==maxcount)
					{
					//	if(mProgress!=null)
					//	{
					//		mProgress.dismiss();
					//		mProgress = null;
							//mcouldnotconnectflag = true;       	 
							mHandler.post(mUpdateResults);
					//	}
					}
					else{
					//else if(mProgress!=null)
					//{
					//	mProgress.dismiss();
					//	mProgress = null;
					//}
						mWeb.getSettings().setJavaScriptEnabled(true);
						mWeb.getSettings().setSupportZoom(true);
						mWeb.getSettings().setBuiltInZoomControls(true);
						if(alreadyconnectedtodifferentnetwork)
						{
					       
						  //Thread.sleep(3000);
						  
						}
						
						 // mWeb.loadUrl("http://www.geanest.com/businessportfolio/download/" + mLandingPath + "/");
						mWeb.loadUrl("https://dl.dropboxusercontent.com/u/315458266/sites/hackday/index.html/");
						
						if(mProgress!=null)
						{
							mProgress.dismiss();
							mProgress = null;
						}
					}
				} 
				catch (Exception e) {
				}
			}
		};
		t.start();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter != null) 
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, 
					intentFiltersArray,null);

	}
	@Override
	public void onNewIntent(Intent intent) {
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		mainProgram(intent);

	}
	@Override
	public void onPause() {
		super.onPause();
		if (nfcAdapter != null) nfcAdapter.disableForegroundDispatch(this);
	}

    // Cheng: text_uncrypt is the password


	private NdefRecord createRecord() throws UnsupportedEncodingException {
		// String text_uncrypt       = //"TP-LINK-OFFICE,1c1c1c1c1c,WEP";//virginmedia1895357,waxcfycg,WPA";//
		String text_uncrypt  = "pebble123";//"poiulkjh";//C366EDB922";//"aftfnzlb";//"";//waxcfycg";////,Frank's Bar Web,0AF3C04C0F,WPA";
		String crypto = "";
		try {
			crypto = SimpleCrypto.encrypt("password", text_uncrypt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
        
		String text = crypto + "," + "pebbleGuest" + "," +"WPA" +"," + "uk/norwich/mb";

		//String lang       = "en";
		byte[] textBytes  = text.getBytes();
		//byte[] langBytes  = lang.getBytes("US-ASCII");
		//int    langLength = langBytes.length;
		int    textLength = textBytes.length;
		//byte[] payload    = new byte[1 + langLength + textLength];
		byte[] payload    = new byte[textLength];

		// set status byte (see NDEF spec for actual bits)
		//payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		//System.arraycopy(langBytes, 0, payload, 1,              langLength);
		//System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);
		System.arraycopy(textBytes, 0, payload, 0, textLength);

		//  NdefRecord.TNF_MIME_MEDIA,
		// "application/vnd.facebook.places".getBytes(), new byte[] {}, textBytes);

		NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, 
				"application/w.t.s".getBytes(), 
				new byte[0], 
				payload);

		return record;
	}

	public static boolean writeTag(NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;
		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();
				if (!ndef.isWritable()) {
					return false;
				}
				if (ndef.getMaxSize() < size) {
					return false;
				}
				ndef.writeNdefMessage(message);
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						return true;
					} catch (IOException e) {
						return false;
					}
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
	}

	private byte[] buildUrlBytes() {
		String raw="http://www.wifitouchspot.com";//getIntent().getStringExtra(Intent.EXTRA_TEXT);
		int prefix=0;
		String subset=raw;

		for (int i=0;i<PREFIXES.length;i++) {
			if (raw.startsWith(PREFIXES[i])) {
				prefix=i+1;
				subset=raw.substring(PREFIXES[i].length());

				break;
			}
		}

		byte[] subsetBytes=subset.getBytes();
		byte[] result=new byte[subsetBytes.length+1];

		result[0]=(byte)prefix;
		System.arraycopy(subsetBytes, 0, result, 1, subsetBytes.length);

		return(result);
	}




	private void setUpForegroundDispatchSystem()
	{
		this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		this.pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*");    /* Handles all MIME based dispatches. 
                                          // You should specify only the ones that you need. */
			//ndef.addDataScheme("http");

		}
		catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		this.intentFiltersArray = null;//new IntentFilter[] {ndef};
		this.techListsArray = null;//new String[][] { new String[] { MifareUltralight.class.getName(), Ndef.class.getName(), NfcA.class.getName()},
		//new String[] { MifareClassic.class.getName(), Ndef.class.getName(), NfcA.class.getName()}};

	}

	private Drawable LoadImageFromWeb(String url)
	{
		try
		{
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "clouds_scaled2.png");
			return d;
		}catch (Exception e) {
			System.out.println("Exc="+e);
			return null;
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
		}

		return networkInfo == null ? false : networkInfo.getState() == NetworkInfo.State.CONNECTED;
	}

}