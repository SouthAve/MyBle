package com.example.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ContentHandler extends DefaultHandler {
	static final String TAG = "ContentHandler";
	String nodeName;
	StringBuilder id;
	StringBuilder name;
	StringBuilder version;

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		id = new StringBuilder();
		name = new StringBuilder();
		version = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		nodeName = localName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		if ("id".equals(nodeName)) {
			id.append(ch, start, length);
		} else if ("name".equals(nodeName)) {
			name.append(ch, start, length);
		} else if ("version".equals(nodeName)) {
			version.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if ("app".equals(localName)) {
			Log.v(TAG, id.toString().trim());
			Log.v(TAG, name.toString().trim());
			Log.v(TAG, version.toString().trim());
			id.setLength(0);
			name.setLength(0);
			version.setLength(0);
		}

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}
}
