package com.odnovolov.forgetmenot.presentation.screen.decksetup.deckcontent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.domain.entity.Card
import com.odnovolov.forgetmenot.presentation.common.SimpleRecyclerViewHolder
import com.odnovolov.forgetmenot.presentation.screen.decksetup.deckcontent.DeckContentEvent.*
import kotlinx.android.synthetic.main.item_card_overview.view.*
import kotlinx.android.synthetic.main.toolbar_deck_content.view.*

class CardOverviewAdapter(
    private val controller: DeckContentController
) : ListAdapter<Card, SimpleRecyclerViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            R.layout.toolbar_deck_content else
            R.layout.item_card_overview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        if (viewType == R.layout.toolbar_deck_content) {
            view.exportButton.run {
                setOnClickListener { controller.dispatch(ExportButtonClicked) }
                TooltipCompat.setTooltipText(this, contentDescription)
            }
            view.searchButton.run {
                setOnClickListener { controller.dispatch(SearchButtonClicked) }
                TooltipCompat.setTooltipText(this, contentDescription)
            }
        }
        return SimpleRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SimpleRecyclerViewHolder, position: Int) {
        if (position == 0) return
        val item: Card = getItem(position)
        with(viewHolder.itemView) {
            questionTextView.text = item.question
            questionTextView.isEnabled = !item.isLearned
            answerTextView.text = item.answer
            answerTextView.isEnabled = !item.isLearned
            cardView.setOnClickListener { controller.dispatch(CardClicked(item.id)) }
        }
    }

    fun submitItems(items: List<Card>) {
        val headerCard = Card(-1, "Faked Card for", "header")
        super.submitList(listOf(headerCard) + items)
    }

    class DiffCallback : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean =
            oldItem == newItem
    }
}