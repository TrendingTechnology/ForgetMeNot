package com.odnovolov.forgetmenot.presentation.screen.search

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.domain.interactor.searcher.SearchCard
import com.odnovolov.forgetmenot.presentation.common.SimpleRecyclerViewHolder
import com.odnovolov.forgetmenot.presentation.common.dp
import com.odnovolov.forgetmenot.presentation.screen.search.SearchEvent.CardClicked
import kotlinx.android.synthetic.main.item_card_overview.view.*

class SearchCardAdapter(
    private val controller: SearchController
) : RecyclerView.Adapter<SimpleRecyclerViewHolder>() {
    var items: List<SearchCard> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_overview, parent, false)
        return SimpleRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SimpleRecyclerViewHolder, position: Int) {
        val item: SearchCard = items[position]
        with(viewHolder.itemView) {
            val param = cardView.layoutParams as ViewGroup.MarginLayoutParams
            val marginTop: Int = if (position == 0) 80.dp else 16.dp
            param.setMargins(
                param.leftMargin,
                marginTop,
                param.rightMargin,
                param.bottomMargin
            )
            questionTextView.text =
                highlight(item.card.question, item.questionMatchingRanges, context)
            questionTextView.isEnabled = !item.card.isLearned
            SpannableStringBuilder(item.card.answer)
            answerTextView.text = highlight(item.card.answer, item.answerMatchingRanges, context)
            answerTextView.isEnabled = !item.card.isLearned
            cardView.setOnClickListener { controller.dispatch(CardClicked(item)) }
        }
    }

    private fun highlight(
        string: String,
        ranges: List<IntRange>,
        context: Context
    ): SpannableString {
        val highlightedColor = ContextCompat.getColor(context, R.color.selected_item_background)
        return SpannableString(string).apply {
            ranges.forEach { selection: IntRange ->
                setSpan(
                    BackgroundColorSpan(highlightedColor),
                    selection.first,
                    selection.last,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    override fun getItemCount(): Int = items.size
}