package com.feztherforeigner.hackathon.capitalone.cameratest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


// http imports


public class OldMainActivity extends Activity {

	Button takeAPictureBt;
	Button anotherButton;
	static String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment())
			.commit();
		}


		/** MY CODE STARTS **/

		// get the button
		takeAPictureBt = (Button) findViewById(R.id.takePictureBt);
		anotherButton = (Button) findViewById(R.id.anotherButton);

		// set the onclicklistener
		takeAPictureBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				takeAPictureBt.setText("I took a picture!");

				try {
					dispatchTakePictureIntent();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		anotherButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				try {
					anotherButton.setText(retrievePictureApache());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	static final int REQUEST_TAKE_PHOTO = 1;

	private void dispatchTakePictureIntent() throws MalformedURLException, IOException {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}

			//sendPictureApache();
		}
	}



	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  // prefix 
				".jpg",         // suffix 
				storageDir      // directory 
				);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();


		takeAPictureBt.setText("picture stored at:\n\n" + mCurrentPhotoPath);



		return image;
	}





/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

	public void sendPicture() throws MalformedURLException, IOException {

		String urlToConnect = "http://capitalfour.herokuapp.com";
		String paramToSend = "fubar";
		File fileToUpload = new File(mCurrentPhotoPath);
		String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.

		URLConnection connection = new URL(urlToConnect).openConnection();
		connection.setDoOutput(true); // This sets request method to POST.
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));

			writer.println("--" + boundary);
			writer.println("Content-Disposition: form-data; name=\"paramToSend\"");
			writer.println("Content-Type: text/plain; charset=UTF-8");
			writer.println();
			writer.println(paramToSend);

			writer.println("--" + boundary);
			writer.println("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"file.txt\"");
			writer.println("Content-Type: text/plain; charset=UTF-8");
			writer.println();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToUpload), "UTF-8"));
				for (String line; (line = reader.readLine()) != null;) {
					writer.println(line);
				}
			} finally {
				if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
			}

			writer.println("--" + boundary + "--");
		} finally {
			if (writer != null) writer.close();
		}

		// Connection is lazily executed whenever you request any status.
		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		System.out.println(responseCode); // Should be 200

	}

	public void sendPictureStack() throws ParseException, IOException {

		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost("http://capitalfour.herokuapp.com");
		File file = new File(mCurrentPhotoPath);

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file, "image/jpeg");
		mpEntity.addPart("userfile", cbFile);


		httppost.setEntity(mpEntity);
		System.out.println("executing request " + httppost.getRequestLine());
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		System.out.println(response.getStatusLine());
		if (resEntity != null) {
			System.out.println(EntityUtils.toString(resEntity));
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		httpclient.getConnectionManager().shutdown();

	}

	/*public void sendPictureSunil() {

		InputStream inputStream;
       Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);          
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeBytes(byte_arr);
            ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("image",image_str));

            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.0.23/Upload_image_ANDROID/upload_image.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String the_string_response = convertResponseToString(response);
                Toast.makeText(UploadImage.this, "Response " + the_string_response, Toast.LENGTH_LONG).show();
            }catch(Exception e){
                  Toast.makeText(UploadImage.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                  System.out.println("Error in http connection "+e.toString());
            }
        }*/

	/*public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

             String res = "";
             StringBuffer buffer = new StringBuffer();
             inputStream = response.getEntity().getContent();
             int contentLength = (int) response.getEntity().getContentLength(); //getting content length�..
             Toast.makeText(UploadImage.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
             if (contentLength < 0){
             }
             else{
                    byte[] data = new byte[512];
                    int len = 0;
                    try
                    {
                        while (-1 != (len = inputStream.read(data)) )
                        {
                            buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer�..
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        inputStream.close(); // closing the stream�..
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    res = buffer.toString();     // converting stringbuffer to string�..

                    Toast.makeText(UploadImage.this, "Result : " + res, Toast.LENGTH_LONG).show();
                    //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
             }
             return res;
        }

	}*/

	public void sendPictureApache() throws ClientProtocolException, IOException {


		//MultipartEntity entity = new MultipartEntity();
		//entity.addPart("file", new FileBody(new File(mCurrentPhotoPath)));

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost("http://capitalfour.herokuapp.com");

		//request.setEntity(entity);

		try {
			HttpResponse response = client.execute(request);
		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		}
	}

	public String retrievePictureApache() throws ClientProtocolException, IOException {


		//MultipartEntity entity = new MultipartEntity();
		//entity.addPart("file", new FileBody(new File(mCurrentPhotoPath)));

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://capitalfour.herokuapp.com");

		//request.setEntity(entity);

		HttpResponse response = null;

		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		}

		return response.getEntity().getContent().toString();
	}


	/**
	 * Insert new file.
	 *
	 * @param service Drive API service instance.
	 * @param title Title of the file to insert, including the extension.
	 * @param description Description of the file to insert.
	 * @param parentId Optional parent folder's ID.
	 * @param mimeType MIME type of the file to insert.
	 * @param filename Filename of the file to insert.
	 * @return Inserted file metadata if successful, {@code null} otherwise.
	 */
	/*private static File insertFile(Drive service, String title, String description,
	      String parentId, String mimeType, String filename) {
	    // File's metadata.
	    File body = new File(mCurrentPhotoPath);//File body = new File();
	    body.setTitle(title);
	    body.setDescription(description);
	    body.setMimeType(mimeType);

	    // Set the parent folder.
	    if (parentId != null && parentId.length() > 0) {
	      body.setParents(
	          Arrays.asList(new ParentReference().setId(parentId)));
	    }

	    // File's content.
	    java.io.File fileContent = new java.io.File(filename);
	    FileContent mediaContent = new FileContent(mimeType, fileContent);
	    try {
	      File file = service.files().insert(body, mediaContent).execute();

	      // Uncomment the following line to print the File ID.
	      // System.out.println("File ID: " + file.getId());

	      return file;
	    } catch (IOException e) {
	      System.out.println("An error occured: " + e);
	      return null;
	    }
	  }*/


}
