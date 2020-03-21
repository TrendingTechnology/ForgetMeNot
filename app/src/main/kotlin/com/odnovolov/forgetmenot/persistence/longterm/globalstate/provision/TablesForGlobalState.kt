package com.odnovolov.forgetmenot.persistence.longterm.globalstate.provision

import com.odnovolov.forgetmenot.persistence.database
import com.odnovolov.forgetmenot.persistence.globalstate.*

class TablesForGlobalState private constructor(
    val deckTable: List<DeckDb>,
    val cardTable: List<CardDb>,
    val exercisePreferenceTable: List<ExercisePreferenceDb>,
    val intervalSchemeTable: List<IntervalSchemeDb>,
    val intervalTable: List<IntervalDb>,
    val pronunciationTable: List<PronunciationDb>,
    val sharedExercisePreferenceTable: List<Long>,
    val sharedIntervalSchemeTable: List<Long>,
    val sharedPronunciationTable: List<Long>
) {
    companion object {
        fun load(): TablesForGlobalState {
            return with(database) {
                TablesForGlobalState(
                    deckTable = deckQueries.selectAll().executeAsList(),
                    cardTable = cardQueries.selectAll().executeAsList(),
                    exercisePreferenceTable = exercisePreferenceQueries.selectAll().executeAsList(),
                    intervalSchemeTable = intervalSchemeQueries.selectAll().executeAsList(),
                    intervalTable = intervalQueries.selectAll().executeAsList(),
                    pronunciationTable = pronunciationQueries.selectAll().executeAsList(),
                    sharedExercisePreferenceTable = sharedExercisePreferenceQueries.selectAll().executeAsList(),
                    sharedIntervalSchemeTable = sharedIntervalSchemeQueries.selectAll().executeAsList(),
                    sharedPronunciationTable = sharedPronunciationQueries.selectAll().executeAsList()
                )
            }
        }
    }
}