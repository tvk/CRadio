package com.senselessweb.cradionative.radio.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class Item
{
	
	private final String name;
	
	private final String src;
	
	private final boolean isPlaylistUrl;

	Item(final String name, final String src, final boolean isPlaylistUrl)
	{
		this.name = name;
		this.src = src;
		this.isPlaylistUrl = isPlaylistUrl;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getUrl()
	{
		try
		{
			return this.isPlaylistUrl ? this.resolvePlaylistUrl(this.src) : this.src;
		} 
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		} 
	}
	
	public JSONObject toJson() throws JSONException
	{
		final JSONObject p = new JSONObject();
		p.put("name", this.getName());
		p.put("src", this.getUrl());
		return p;
	}
	
	private String resolvePlaylistUrl(final String src) throws IOException
	{
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				new URL(src).openConnection().getInputStream()));
		String result = null;
		for (String line = reader.readLine(); line != null; line = reader.readLine())
		{
			// In m3u files, the uri is just the first line
			if (result == null)
				result = line;
			
			// Otherwise it is the first line that starts with "File="
			if (line.startsWith("File")) 
			{
				result = line.substring(line.indexOf("=") + 1);
				break;
			}
		}
		try
		{
			reader.close();
		}
		catch (IOException e) {};
		return result;
	}
}
