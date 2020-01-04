package com.odnovolov.forgetmenot.screen.decksettings

import com.odnovolov.forgetmenot.common.base.BaseController
import com.odnovolov.forgetmenot.common.entity.NameCheckResult
import com.odnovolov.forgetmenot.common.entity.NameCheckResult.*
import com.odnovolov.forgetmenot.common.entity.PresetNameInputDialogStatus
import com.odnovolov.forgetmenot.common.entity.PresetNameInputDialogStatus.*
import com.odnovolov.forgetmenot.common.database.database
import com.odnovolov.forgetmenot.common.database.nameCheckResultAdapter
import com.odnovolov.forgetmenot.common.database.presetNameInputDialogStatusAdapter
import com.odnovolov.forgetmenot.screen.decksettings.DeckSettingsEvent.*
import com.odnovolov.forgetmenot.screen.decksettings.DeckSettingsOrder.*

class DeckSettingsController : BaseController<DeckSettingsEvent, DeckSettingsOrder>() {
    private val queries: DeckSettingsControllerQueries = database.deckSettingsControllerQueries

    override fun handleEvent(event: DeckSettingsEvent) {
        when (event) {
            RenameDeckButtonClicked -> {
                issueOrder(ShowRenameDeckDialog)
            }

            SaveExercisePreferenceButtonClicked -> {
                setPresetNameInputDialogStatus(VisibleToMakeIndividualPresetAsShared)
            }

            is SetExercisePreferenceButtonClicked -> {
                queries.setCurrentExercisePreference(event.id)
            }

            is RenameExercisePreferenceButtonClicked -> {
                val name = queries.getExercisePreferenceNameById(event.id).executeAsOneOrNull()
                if (!name.isNullOrEmpty()) {
                    queries.setRenamePresetId(event.id)
                    setPresetNameInputDialogStatus(VisibleToRenameSharedPreset)
                    issueOrder(SetDialogText(name))
                }
            }

            is DeleteExercisePreferenceButtonClicked -> {
                queries.deleteSharedExercisePreference(event.id)
            }

            AddNewExercisePreferenceButtonClicked -> {
                setPresetNameInputDialogStatus(VisibleToCreateNewSharedPreset)
            }

            is DialogTextChanged -> {
                queries.setTypedPresetName(event.text)
                checkName()
            }

            PositiveDialogButtonClicked -> {
                if (checkName() === OK) {
                    when (getNameInputDialogStatus()) {
                        VisibleToMakeIndividualPresetAsShared -> {
                            queries.renameCurrentPreset()
                        }
                        VisibleToCreateNewSharedPreset -> {
                            queries.createNewSharedExercisePreference()
                            queries.bindNewExercisePreferenceToDeck()
                        }
                        VisibleToRenameSharedPreset -> {
                            queries.renameSharedPreset()
                        }
                        else -> {
                        }
                    }
                    setPresetNameInputDialogStatus(Invisible)
                }
            }

            NegativeDialogButtonClicked -> {
                setPresetNameInputDialogStatus(Invisible)
            }

            RandomOrderSwitchToggled -> {
                queries.toggleRandomOrder()
            }

            is TestMethodWasSelected -> {
                queries.setTestMethod(event.testMethod)
            }

            IntervalsButtonClicked -> {
                queries.cleanIntervalsState()
                queries.initIntervalsState()
                issueOrder(NavigateToIntervals)
            }

            PronunciationButtonClicked -> {
                queries.cleanPronunciationState()
                queries.initPronunciationState()
                issueOrder(NavigateToPronunciation)
            }

            DisplayQuestionSwitchToggled -> {
                queries.toggleIsQuestionDisplayed()
            }

            is CardReverseWasSelected -> {
                queries.setCardReverse(event.cardReverse)
            }
        }
    }

    private fun setPresetNameInputDialogStatus(status: PresetNameInputDialogStatus) {
        val databaseValue = presetNameInputDialogStatusAdapter.encode(status)
        queries.setPresetNameInputDialogStatus(databaseValue)
    }

    private fun getNameInputDialogStatus(): PresetNameInputDialogStatus {
        val databaseValue = queries.getPresetNameInputDialogStatus().executeAsOne()
        return presetNameInputDialogStatusAdapter.decode(databaseValue)
    }

    private fun checkName(): NameCheckResult {
        val nameCheckResult = when {
            queries.isTypedPresetNameEmpty().executeAsOne() -> EMPTY
            queries.isTypedPresetNameOccupied().executeAsOne() -> OCCUPIED
            else -> OK
        }
        queries.setNameCheckResult(nameCheckResultAdapter.encode(nameCheckResult))
        return nameCheckResult
    }
}