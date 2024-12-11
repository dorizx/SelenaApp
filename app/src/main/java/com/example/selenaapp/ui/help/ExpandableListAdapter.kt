package com.example.selenaapp.ui.help

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.selenaapp.R

class ExpandableListAdapter(
    private val context: Context,
    private val questions: List<String>,
    private val answers: List<List<Pair<String, Int?>>>
) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return answers[groupPosition][childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return answers[groupPosition].size
    }

    override fun getGroup(groupPosition: Int): Any {
        return questions[groupPosition]
    }

    override fun getGroupCount(): Int {
        return questions.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.child_item, parent, false)
        val stepTextView = view.findViewById<TextView>(R.id.stepText)
        val stepImageView = view.findViewById<ImageView>(R.id.stepImage)

        val step = answers[groupPosition][childPosition]

        // Set teks langkah
        stepTextView.text = step.first

        // Set gambar jika ada
        if (step.second != null) {
            stepImageView.visibility = View.VISIBLE
            stepImageView.setImageResource(step.second!!)
        } else {
            stepImageView.visibility = View.GONE
        }

        return view
    }


    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.group_item, parent, false)
        val groupTextView = view.findViewById<TextView>(R.id.textGroup)
        groupTextView.text = questions[groupPosition]
        return view
    }

}