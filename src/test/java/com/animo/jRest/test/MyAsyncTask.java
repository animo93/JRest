package com.animo.jRest.test;
import com.animo.jRest.util.AsyncTask;

/**
 * Created by animo on 20/12/17.
 */

public class MyAsyncTask{
	
	public static void main(String args[]) throws Exception{
		new AsyncTask<String, String>() {

			@Override
			protected String runInBackground(String params) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "Hello "+params;
			}


			@Override
			protected void preExecute() {
				System.out.println("Loading for 10 secs");
				
			}

			@Override
			protected void postExecute(String result, Exception e) {
				// TODO Auto-generated method stub
				
			}

		}.executeLater("World");
		new AsyncTask<String, String>() {

			@Override
			protected String runInBackground(String params) {
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "Hello "+params;
			}


			@Override
			protected void preExecute() {
				System.out.println("Loading for 2 secs");
				
			}

			@Override
			protected void postExecute(String result, Exception e) {
				// TODO Auto-generated method stub
				
			}

		}.executeLater("World");
		
		new AsyncTask<String, String>() {

			@Override
			protected String runInBackground(String params) {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "Hello "+params;
			}


			@Override
			protected void preExecute() {
				System.out.println("Loading for 5 secs");
				
			}

			@Override
			protected void postExecute(String result, Exception e) {
				// TODO Auto-generated method stub
				
			}

		}.executeLater("World");
	}

}
