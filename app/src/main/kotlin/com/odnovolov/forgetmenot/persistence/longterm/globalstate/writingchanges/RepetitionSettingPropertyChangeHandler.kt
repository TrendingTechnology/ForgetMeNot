package com.odnovolov.forgetmenot.persistence.longterm.globalstate.writingchanges

import com.odnovolov.forgetmenot.Database
import com.odnovolov.forgetmenot.domain.architecturecomponents.PropertyChangeRegistry
import com.odnovolov.forgetmenot.domain.architecturecomponents.PropertyChangeRegistry.Change.PropertyValueChange
import com.odnovolov.forgetmenot.domain.entity.RepetitionSetting
import com.odnovolov.forgetmenot.persistence.longterm.PropertyChangeHandler
import com.soywiz.klock.DateTimeSpan

class RepetitionSettingPropertyChangeHandler(
    database: Database
) : PropertyChangeHandler {
    private val queries = database.repetitionSettingQueries

    override fun handle(change: PropertyChangeRegistry.Change) {
        if (change !is PropertyValueChange) return
        val repetitionSettingId: Long = change.propertyOwnerId
        val exists: Boolean = queries.exists(repetitionSettingId).executeAsOne()
        if (!exists) return
        when (change.property) {
            RepetitionSetting::name -> {
                val name = change.newValue as String
                queries.updateName(name, repetitionSettingId)
            }
            RepetitionSetting::isAvailableForExerciseCardsIncluded -> {
                val isAvailableForExerciseCardsIncluded = change.newValue as Boolean
                queries.updateIsAvailableForExerciseCardsIncluded(
                    isAvailableForExerciseCardsIncluded,
                    repetitionSettingId
                )
            }
            RepetitionSetting::isAwaitingCardsIncluded -> {
                val isAwaitingCardsIncluded = change.newValue as Boolean
                queries.updateIsAwaitingCardsIncluded(isAwaitingCardsIncluded, repetitionSettingId)
            }
            RepetitionSetting::isLearnedCardsIncluded -> {
                val isLearnedCardsIncluded = change.newValue as Boolean
                queries.updateIsLearnedCardsIncluded(isLearnedCardsIncluded, repetitionSettingId)
            }
            RepetitionSetting::levelOfKnowledgeRange -> {
                val levelOfKnowledgeRange = change.newValue as IntRange
                queries.updateLevelOfKnowledgeRange(
                    levelOfKnowledgeRange.first,
                    levelOfKnowledgeRange.last,
                    repetitionSettingId
                )
            }
            RepetitionSetting::lastAnswerFromTimeAgo -> {
                val lastAnswerFromTimeAgo = change.newValue as DateTimeSpan?
                queries.updateLastAnswerFromTimeAgo(lastAnswerFromTimeAgo, repetitionSettingId)
            }
            RepetitionSetting::lastAnswerToTimeAgo -> {
                val lastAnswerToTimeAgo = change.newValue as DateTimeSpan?
                queries.updateLastAnswerToTimeAgo(lastAnswerToTimeAgo, repetitionSettingId)
            }
            RepetitionSetting::numberOfLaps -> {
                val numberOfLaps = change.newValue as Int
                queries.updateNumberOfLaps(numberOfLaps, repetitionSettingId)
            }
        }
    }
}