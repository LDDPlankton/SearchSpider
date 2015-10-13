package com.webspider.main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.webspider.dao.SiteDAO;
import com.webspider.models.SiteModel;

public class Main
{
	public static void seed()
	{
		DBSeeder seeder = new DBSeeder();
		seeder.seed();
	}
	
	
	public static void main(String[] args)
	{
		//DO SEEDING
		seed();
				
		//LinkTool lt = new LinkTool();
		//lt.test();
		
		SharedResource mySharedResource = new SharedResource();

		//LOOP THROUGH THREAD POOL
		ExecutorService es = Executors.newCachedThreadPool();
		for(int i=0;i<1;i++)
		{
			Runnable worker = new DomainSpider(mySharedResource); 
		    es.execute( new DomainSpider(mySharedResource) );
		    try
		    {
				Thread.sleep( (1000 *5) );
			}
		    catch (InterruptedException e)
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		es.shutdown();
		//WAIT FOR THREAD TERMINATION
		while (!es.isTerminated()) { }
		
		System.out.println("FINISHED!");

		mySharedResource.print();

		/*
		//LOOP THROUGH THREAD POOL
		ExecutorService es = Executors.newCachedThreadPool();
		for(int i=0;i<5;i++)
		{
		    es.execute( new TestThread(mySharedResource) );
		}
		es.shutdown();
		//WAIT FOR THREAD TERMINATION
		while (!es.isTerminated()) { }

		mySharedResource.print();
		*/
		System.exit(1);		
	}

}
