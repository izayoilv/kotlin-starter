package com.example.notesapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
  private val context: Context,
  private var notes: List<Note>,
  private var fontSizeSp: Float,
) : BaseAdapter() {

  private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

  fun submit(notes: List<Note>, fontSizeSp: Float) {
    this.notes = notes
    this.fontSizeSp = fontSizeSp
    notifyDataSetChanged()
  }

  override fun getCount(): Int = notes.size

  override fun getItem(position: Int): Note = notes[position]

  override fun getItemId(position: Int): Long = notes[position].id

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
    val note = notes[position]
    val title = view.findViewById<TextView>(R.id.noteTitle)
    val meta = view.findViewById<TextView>(R.id.noteMeta)
    title.text = note.title
    title.textSize = fontSizeSp
    meta.text = "${note.author} • ${dateFormat.format(Date(note.createdAt))}"
    return view
  }
}
