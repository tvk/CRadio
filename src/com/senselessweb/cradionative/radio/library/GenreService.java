package com.senselessweb.cradionative.radio.library;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.util.Log;

import com.google.common.collect.Lists;

public class GenreService
{
	private static final String genresLocation = "http://v-serv.dyndns.org/remote-radio/genres.txt";
	
	private static final String shoutcastRequestUrl = "http://www.shoutcast.com/search-ajax/";

	private static final List<NameValuePair> parameters = Lists.<NameValuePair>newArrayList(
			new BasicNameValuePair("count", "15"),
			new BasicNameValuePair("mode", "listenershead2"),
			new BasicNameValuePair("order", "desc2"));

	private final List<String> genres = new ArrayList<String>();
	
	private int genrePointer = 0;
	
	private final Map<String, List<Item>> itemsByGenre = new HashMap<String, List<Item>>();
	
	private int itemPointer = 0;
	
	public GenreService()
	{
		try
		{
			Log.d(GenreService.class.toString(), "Loading genres");
			final BufferedReader reader = new BufferedReader(new InputStreamReader(
					new URL(genresLocation).openConnection().getInputStream()));
			for (String line = reader.readLine(); line != null; line = reader.readLine())
				this.genres.add(line);
			Log.d(GenreService.class.toString(), "Loaded " + this.genres.size() + " genres");
			this.loadGenreItems(this.genres.get(0));
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public Item getCurrent()
	{
		return this.getCurrentGenreItems().get(this.itemPointer);
	}
	
	public Item getCurrentIfAvailable()
	{
		return this.itemsByGenre.containsKey(this.getCurrentGenre()) ?
				this.getCurrent() : null;
	}
	
	public void nextGenre()
	{
		this.genrePointer++;
		if (this.genrePointer >= this.genres.size()) this.genrePointer = 0;
		this.itemPointer = 0;
	}
	
	public void previousGenre()
	{
		this.genrePointer--;
		if (this.genrePointer < 0) this.genrePointer = this.genres.size() - 1;
		this.itemPointer = 0;
	}
	
	public void nextStation()
	{
		this.itemPointer++;
		if (this.itemPointer >= this.getCurrentGenreItems().size()) this.itemPointer = 0;
	}
	
	public void previousStation()
	{
		this.itemPointer--;
		if (this.itemPointer < 0) this.itemPointer = this.getCurrentGenreItems().size() - 1;
	}
	
	private List<Item> getCurrentGenreItems()
	{
		final String genre = this.genres.get(this.genrePointer);
		if (!this.itemsByGenre.containsKey(genre))
		{
			this.loadGenreItems(genre);
			this.itemPointer = 0;
		}
		return this.itemsByGenre.get(genre);
	}
	
	private void loadGenreItems(final String genre)
	{
		Log.d(GenreService.class.toString(), "Loading items for " + genre);
		try
		{
			// Send the request
			final HttpClient client = new DefaultHttpClient();
			final HttpPost post = new HttpPost(shoutcastRequestUrl + URLEncoder.encode(genre));
			post.setEntity(new UrlEncodedFormEntity(parameters));
			
			final String response = client.execute(post, new BasicResponseHandler());
		
			// Parse the response
			final Document document = Jsoup.parse(response);
			final List<Item> result = new ArrayList<Item>();
			for (final Element element : document.getElementsByClass("dirlist"))
			{
				final String url = element.select("a.clickabletitle").attr("href");
				final String title = element.select("a.clickabletitle").text();
				result.add(new Item(genre + " - " + title, url, true));
			}
			this.itemsByGenre.put(genre, result);
			Log.d(GenreService.class.toString(), "Loaded " + this.itemsByGenre.get(genre).size() + " items");
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String getCurrentGenre()
	{
		return this.genres.get(this.genrePointer);
	}
}
