package com.deng.mychat.io;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONObject;

import com.deng.mychat.auth.Client;
import com.deng.mychat.auth.JSONObjectRet;
import com.deng.mychat.config.Conf;
import com.deng.mychat.util.IOnProcess;
import com.deng.mychat.util.InputStreamAt;
import com.deng.mychat.util.MultipartEntity;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class IO {

	public static String UNDEFINED_KEY = null;
	private static Client mClient;
	private static String mUptoken;
	private static long mClientUseTime;
	public IO(Client client, String uptoken) {
		mClient = client;
		mUptoken = uptoken;
	}

	private static Client defaultClient() {
		if (mClient != null && System.currentTimeMillis() - mClientUseTime > 3 * 60 * 1000) { // 1 minute
			mClient.close();
			mClient = null;
		}
		if (mClient == null) {
			mClient = Client.defaultClient();
		}
		mClientUseTime = System.currentTimeMillis();
		return mClient;
	}

	/**
	 * 涓婁紶浜岃繘鍒?	 *
	 * @param key	  閿?鍚? UNDEFINED_KEY 琛ㄧず鑷姩鐢熸垚key
	 * @param isa	  浜岃繘鍒舵暟鎹?	 * @param extra   涓婁紶鍙傛暟
	 * @param ret	  鍥炶皟鍑芥暟
	 */
	public void put(String key, InputStreamAt isa, PutExtra extra, JSONObjectRet ret) {
		MultipartEntity m = new MultipartEntity();
		if (key != null) m.addField("key", key);
		if (extra.checkCrc == PutExtra.AUTO_CRC32) {
			try {
				extra.crc32 = isa.crc32();
			} catch (IOException e) {
				ret.onFailure(e);
				return;
			}
		}
		if (extra.checkCrc != PutExtra.UNUSE_CRC32) m.addField("crc32", extra.crc32 + "");
		for (Map.Entry<String, String> i: extra.params.entrySet()) {
			m.addField(i.getKey(), i.getValue());
		}

		m.addField("token", mUptoken);
		m.addFile("file", extra.mimeType, key == null ? "?" : key, isa);

		Client client = defaultClient();
		final Client.ClientExecutor executor = client.makeClientExecutor();
		m.setProcessNotify(new IOnProcess() {
			@Override
			public void onProcess(long current, long total) {
				executor.upload(current, total);
			}

			@Override
			public void onFailure(Exception ex) {
				executor.onFailure(ex);
			}
		});
		client.call(executor, Conf.UP_HOST, m, ret);
	}

	/**
	 * 閫氳繃鎻愪緵URI鏉ヤ笂浼犳寚瀹氱殑鏂囦欢
	 *
	 * @param mContext
	 * @param key
	 * @param uri 閫氳繃鍥惧簱鎴栧叾浠栨嬁鍒扮殑URI
	 * @param extra 涓婁紶鍙傛暟
	 * @param ret 缁撴灉鍥炶皟鍑芥暟
	 */
	public void putFile(Context mContext, String key, Uri uri, PutExtra extra, final JSONObjectRet ret) {
		if (!uri.toString().startsWith("file")) uri = convertFileUri(mContext, uri);
		try {
			File file = new File(new URI(uri.toString()));
			if (file.exists()) {
				putAndClose(key, InputStreamAt.fromFile(file), extra, ret);
				return;
			}
			ret.onFailure(new Exception("file not exist: " + uri.toString()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			ret.onFailure(e);
		}
	}
	public void putFile(String key, File file, PutExtra extra, JSONObjectRet callback) {
		putAndClose(key, InputStreamAt.fromFile(file), extra, callback);
	}

	public void putAndClose(final String key, final InputStreamAt input, final PutExtra extra, final JSONObjectRet ret) {
		put(key, input, extra, new JSONObjectRet() {
			@Override
			public void onSuccess(JSONObject obj) {
				input.close();
				ret.onSuccess(obj);
			}

			@Override
			public void onProcess(long current, long total) {
				ret.onProcess(current, total);
			}

			@Override
			public void onPause(Object tag) {
				ret.onPause(tag);
			}

			@Override
			public void onFailure(Exception ex) {
				input.close();
				ret.onFailure(ex);
			}
		});
	}

	public static Uri convertFileUri(Context mContext, Uri uri) {
		String filePath;
		if (uri != null && "content".equals(uri.getScheme())) {
			Cursor cursor = mContext.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
			cursor.moveToFirst();
			filePath = cursor.getString(0);
			cursor.close();
		} else {
			filePath = uri.getPath();
		}
		return Uri.parse("file://" + filePath);
	}

	public static void put(String uptoken, String key, InputStreamAt input, PutExtra extra, JSONObjectRet callback) {
		new IO(defaultClient(), uptoken).put(key, input, extra, callback);
	}

	public static void putFile(Context mContext, String uptoken, String key, Uri uri, PutExtra extra, JSONObjectRet callback) {
		new IO(defaultClient(), uptoken).putFile(mContext, key, uri, extra, callback);
	}
	public static void putFile(String uptoken, String key, File file, PutExtra extra, JSONObjectRet callback) {
		new IO(defaultClient(), uptoken).putFile(key, file, extra, callback);
	}
}

