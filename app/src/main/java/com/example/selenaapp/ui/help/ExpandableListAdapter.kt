package com.example.selenaapp.ui.help

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class ExpandableListAdapter(
    private val context: Context,
    private val questions: List<String>,
    private val answers: List<List<String>>
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
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(android.R.layout.simple_expandable_list_item_2, null)
        }
        val answerTextView = view?.findViewById<TextView>(android.R.id.text2)
        answerTextView?.text = answers[groupPosition][childPosition]
        return view!!
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null)
        }
        val questionTextView = view?.findViewById<TextView>(android.R.id.text1)
        questionTextView?.text = questions[groupPosition]
        return view!!
    }
}