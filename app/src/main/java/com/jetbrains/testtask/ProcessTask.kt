package com.jetbrains.testtask

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.widget.EditText
import android.widget.TextView
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.HashMap

class ProcessTask() : AsyncTask<Void, Void, List<String>>() {
    private lateinit var context : WeakReference<Context>

    private lateinit var text1: String

    private lateinit var text2: String

    private lateinit var textView: WeakReference<TextView>


    constructor(parent: Context) : this() {
        this.context = WeakReference(parent)
    }

    override fun doInBackground(vararg params: Void?): List<String>? {
        val delims = arrayOf(" ", ".", ",", ";", "!", "?", ":", "(", ")", "\"")
        val arrayText1 = text1.split(*delims)
        val arrayText2 = text2.split(*delims)
        val stopWords = context.get()?.resources?.openRawResource(R.raw.stop_words)?.bufferedReader()?.readLines()

        val set1 = HashMap<String, Int>();
        val set2 = HashMap<String, Int>();
        for(it in arrayText1)
            if(!stopWords?.contains(it)!!)
                set1.put(it, set1.getOrDefault(it, 0) + 1)

        for(it in arrayText2)
            if(!stopWords?.contains(it)!!)
                set2.put(it, set2.getOrDefault(it, 0) + 1)

        val resultSet = ArrayList<Pair<Int, String>>()
        for(it in set1.keys.intersect(set2.keys))
            resultSet.add(Pair(set1.getValue(it) + set2.getValue(it), it))

        resultSet.sortWith(kotlin.Comparator { t2, t1 ->  t1.first.compareTo(t2.first)})
        return resultSet.map { it.second }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        text1 = (context.get() as Activity).findViewById<EditText>(R.id.editText).text.toString().toLowerCase()
        text2 = (context.get() as Activity).findViewById<EditText>(R.id.editText1).text.toString().toLowerCase()
        textView = WeakReference((context.get() as Activity).findViewById(R.id.textView))
    }

    override fun onPostExecute(result: List<String>?) {
        super.onPostExecute(result)
        textView.get()?.text = ""
        if (result != null) {
            for(it in result)
                textView.get()?.append("$it ")
        }
    }
}