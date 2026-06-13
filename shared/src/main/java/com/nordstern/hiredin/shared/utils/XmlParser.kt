package com.nordstern.hiredin.shared.utils

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

object XmlParser {
    fun parseSimple(xml: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(xml.reader())
        var event = parser.eventType
        var currentTag: String? = null
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> currentTag = parser.name
                XmlPullParser.TEXT -> currentTag?.let { result[it] = parser.text.trim() }
                XmlPullParser.END_TAG -> currentTag = null
            }
            event = parser.next()
        }
        return result
    }
}
