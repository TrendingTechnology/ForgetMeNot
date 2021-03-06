package com.odnovolov.forgetmenot.presentation.screen.home

import com.odnovolov.forgetmenot.domain.entity.Deck
import com.odnovolov.forgetmenot.domain.entity.GlobalState
import com.odnovolov.forgetmenot.domain.interactor.deckeditor.DeckEditor
import com.odnovolov.forgetmenot.domain.interactor.deckexporter.DeckExporter
import com.odnovolov.forgetmenot.domain.interactor.deckremover.DeckRemover
import com.odnovolov.forgetmenot.domain.interactor.deckremover.DeckRemover.Event.DecksHasRemoved
import com.odnovolov.forgetmenot.domain.interactor.exercise.Exercise
import com.odnovolov.forgetmenot.domain.interactor.exercise.ExerciseStateCreator
import com.odnovolov.forgetmenot.domain.interactor.repetition.RepetitionStateCreator
import com.odnovolov.forgetmenot.presentation.common.LongTermStateSaver
import com.odnovolov.forgetmenot.presentation.common.Navigator
import com.odnovolov.forgetmenot.presentation.common.ShortTermStateProvider
import com.odnovolov.forgetmenot.presentation.common.base.BaseController
import com.odnovolov.forgetmenot.presentation.common.customview.preset.PresetDialogState
import com.odnovolov.forgetmenot.presentation.common.firstBlocking
import com.odnovolov.forgetmenot.presentation.screen.decksetup.DeckSetupDiScope
import com.odnovolov.forgetmenot.presentation.screen.decksetup.DeckSetupScreenState
import com.odnovolov.forgetmenot.presentation.screen.exercise.ExerciseDiScope
import com.odnovolov.forgetmenot.presentation.screen.help.HelpDiScope
import com.odnovolov.forgetmenot.presentation.screen.home.HomeController.Command
import com.odnovolov.forgetmenot.presentation.screen.home.HomeController.Command.*
import com.odnovolov.forgetmenot.presentation.screen.home.HomeEvent.*
import com.odnovolov.forgetmenot.presentation.screen.repetitionsettings.RepetitionSettingsDiScope
import com.odnovolov.forgetmenot.presentation.screen.search.SearchDiScope
import com.odnovolov.forgetmenot.presentation.screen.settings.SettingsDiScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.IOException

class HomeController(
    private val homeScreenState: HomeScreenState,
    private val deckReviewPreference: DeckReviewPreference,
    private val deckExporter: DeckExporter,
    private val deckRemover: DeckRemover,
    private val exerciseStateCreator: ExerciseStateCreator,
    private val globalState: GlobalState,
    private val navigator: Navigator,
    private val longTermStateSaver: LongTermStateSaver,
    private val homeScreenStateProvider: ShortTermStateProvider<HomeScreenState>
) : BaseController<HomeEvent, Command>() {
    sealed class Command {
        object ShowNoCardIsReadyForExerciseMessage : Command()
        class ShowDeckRemovingMessage(val numberOfDecksRemoved: Int) : Command()
        class ShowCreateFileDialog(val fileName: String) : Command()
        object ShowDeckIsExportedMessage : Command()
        class ShowExportErrorMessage(val e: IOException) : Command()
    }

    init {
        deckRemover.events.onEach { event: DeckRemover.Event ->
            when (event) {
                is DecksHasRemoved -> {
                    sendCommand(ShowDeckRemovingMessage(numberOfDecksRemoved = event.count))
                }
            }
        }
            .launchIn(coroutineScope)
    }


    lateinit var displayedDeckIds: Flow<List<Long>>

    override fun handle(event: HomeEvent) {
        when (event) {
            is SearchTextChanged -> {
                homeScreenState.searchText = event.searchText
            }

            SearchInCardsButtonClicked -> {
                homeScreenState.selectedDeckIds = emptyList()
                navigator.navigateToSearchFromHome {
                    SearchDiScope(homeScreenState.searchText)
                }
            }

            DisplayOnlyWithTasksCheckboxClicked -> {
                with(deckReviewPreference) { displayOnlyWithTasks = !displayOnlyWithTasks }
            }

            SettingsButtonClicked -> {
                homeScreenState.selectedDeckIds = emptyList()
                navigator.navigateToSettings { SettingsDiScope() }
            }

            HelpButtonClicked -> {
                homeScreenState.selectedDeckIds = emptyList()
                navigator.navigateToHelpFromHome { HelpDiScope() }
            }

            is DeckButtonClicked -> {
                if (homeScreenState.selectedDeckIds.isNotEmpty()) {
                    toggleDeckSelection(event.deckId)
                } else {
                    startExercise(deckIds = listOf(event.deckId))
                }
            }

            is DeckButtonLongClicked -> {
                toggleDeckSelection(event.deckId)
            }

            is RepetitionModeMenuItemClicked -> {
                startRepetitionSettings(deckIds = listOf(event.deckId))
            }

            RepetitionModeMultiSelectMenuItemClicked -> {
                startRepetitionSettings(homeScreenState.selectedDeckIds)
            }

            is SetupDeckMenuItemClicked -> {
                homeScreenState.selectedDeckIds = emptyList()
                navigator.navigateToDeckSetupFromHome {
                    val deck: Deck = globalState.decks.first { it.id == event.deckId }
                    val deckEditorState = DeckEditor.State(deck)
                    DeckSetupDiScope.create(DeckSetupScreenState(deck), deckEditorState)
                }
            }

            is ExportMenuItemClicked -> {
                val deck: Deck = globalState.decks.first { it.id == event.deckId }
                homeScreenState.exportedDeck = deck
                val fileName = deck.name
                sendCommand(ShowCreateFileDialog(fileName))
            }

            is OutputStreamOpened -> {
                try {
                    deckExporter.export(
                        deck = homeScreenState.exportedDeck ?: return,
                        outputStream = event.outputStream
                    )
                    sendCommand(ShowDeckIsExportedMessage)
                } catch (e: IOException) {
                    sendCommand(ShowExportErrorMessage(e))
                }
            }

            is RemoveDeckMenuItemClicked -> {
                deckRemover.removeDeck(event.deckId)
            }

            DecksRemovedSnackbarCancelActionClicked -> {
                deckRemover.restoreDecks()
            }

            StartExerciseMenuItemClicked -> {
                startExercise(homeScreenState.selectedDeckIds)
            }

            SelectAllDecksMenuItemClicked -> {
                homeScreenState.selectedDeckIds = displayedDeckIds.firstBlocking()
            }

            RemoveDecksMenuItemClicked -> {
                deckRemover.removeDecks(homeScreenState.selectedDeckIds)
                homeScreenState.selectedDeckIds = emptyList()
            }

            ActionModeFinished -> {
                homeScreenState.selectedDeckIds = emptyList()
            }
        }
    }

    private fun startRepetitionSettings(deckIds: List<Long>) {
        homeScreenState.selectedDeckIds = emptyList()
        navigator.navigateToRepetitionSettings {
            val decks: List<Deck> = globalState.decks.filter { it.id in deckIds }
            val repetitionCreatorState = RepetitionStateCreator.State(decks)
            RepetitionSettingsDiScope.create(repetitionCreatorState, PresetDialogState())
        }
    }

    private fun startExercise(deckIds: List<Long>) {
        if (exerciseStateCreator.hasAnyCardAvailableForExercise(deckIds)) {
            homeScreenState.selectedDeckIds = emptyList()
            navigator.navigateToExercise {
                val exerciseState: Exercise.State =
                    exerciseStateCreator.create(deckIds)
                ExerciseDiScope.create(exerciseState)
            }
        } else {
            sendCommand(ShowNoCardIsReadyForExerciseMessage)
        }
    }

    private fun toggleDeckSelection(deckId: Long) {
        with(homeScreenState) {
            selectedDeckIds = if (deckId in selectedDeckIds) {
                selectedDeckIds - deckId
            } else {
                selectedDeckIds + deckId
            }
        }
    }

    override fun saveState() {
        longTermStateSaver.saveStateByRegistry()
        homeScreenStateProvider.save(homeScreenState)
    }
}