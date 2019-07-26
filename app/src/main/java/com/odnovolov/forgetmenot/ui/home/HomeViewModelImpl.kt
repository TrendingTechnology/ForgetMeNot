package com.odnovolov.forgetmenot.ui.home

import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.odnovolov.forgetmenot.entity.Deck
import com.odnovolov.forgetmenot.common.LiveEvent
import com.odnovolov.forgetmenot.ui.adddeck.AddDeckDao
import com.odnovolov.forgetmenot.ui.adddeck.AddDeckViewModel
import com.odnovolov.forgetmenot.ui.adddeck.AddDeckViewModel.Event.AddDeckRequested
import com.odnovolov.forgetmenot.ui.adddeck.AddDeckViewModelImpl
import com.odnovolov.forgetmenot.ui.exercisecreator.ExerciseCreatorViewModel
import com.odnovolov.forgetmenot.ui.exercisecreator.ExerciseCreatorViewModel.Event.CreateExercise
import com.odnovolov.forgetmenot.ui.home.DeckSorting.*
import com.odnovolov.forgetmenot.ui.home.HomeViewModel.*
import com.odnovolov.forgetmenot.ui.home.HomeViewModel.Action.*
import com.odnovolov.forgetmenot.ui.home.HomeViewModel.Event.*

class HomeViewModelImpl(
    private val repository: HomeRepository,
    override val addDeckViewModel: AddDeckViewModel,
    override val exerciseCreatorViewModel: ExerciseCreatorViewModel
) : ViewModel(), HomeViewModel {

    class Factory(
        owner: SavedStateRegistryOwner,
        private val homeRepository: HomeRepository,
        private val addDeckDao: AddDeckDao,
        private val exerciseCreatorViewModel: ExerciseCreatorViewModel
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            val addDeckViewModel = AddDeckViewModelImpl(addDeckDao, handle)
            return HomeViewModelImpl(homeRepository, addDeckViewModel, exerciseCreatorViewModel) as T
        }
    }

    private val decks: LiveData<List<Deck>> = repository.getDecks()
    private val searchText = MutableLiveData("")

    private val deckSorting: MutableLiveData<DeckSorting> = repository.getDeckSorting(initialValue = BY_TIME_CREATED)
    private val decksPreview = MediatorLiveData<List<DeckPreview>>().apply {
        fun updateValue() {
            var decks = decks.value ?: return
            val searchText = searchText.value!!
            val deckSorting = deckSorting.value ?: return

            if (searchText.isNotEmpty()) {
                decks = decks.filter { it.name.contains(searchText) }
            }
            decks = when (deckSorting) {
                BY_TIME_CREATED -> decks.sortedBy { it.createdAt }
                BY_NAME -> decks.sortedBy { it.name }
                BY_LAST_OPENED -> decks.sortedByDescending { it.lastOpenedAt }
            }
            this.value = decks
                .map { deck: Deck ->
                    val passedLaps: Int = deck.cards
                        .filter { card -> !card.isLearned }
                        .map { card -> card.lap }
                        .min() ?: 0
                    val progress = DeckPreview.Progress(
                        learned = deck.cards.filter { it.isLearned }.size,
                        total = deck.cards.size
                    )
                    DeckPreview(
                        deck.id,
                        deck.name,
                        passedLaps,
                        progress
                    )
                }
        }

        addSource(decks) { updateValue() }
        addSource(searchText) { updateValue() }
        addSource(deckSorting) { updateValue() }
    }

    override val state = State(
        decksPreview,
        deckSorting
    )

    private val actionSender = LiveEvent<Action>()
    override val action: LiveData<Action> = actionSender

    override fun onEvent(event: Event) {
        when (event) {
            AddDeckButtonClicked -> {
                addDeckViewModel.onEvent(AddDeckRequested)
            }
            is DeckButtonClicked -> {
                val deck = decks.value!!.find { it.id == event.deckId }!!
                exerciseCreatorViewModel.onEvent(CreateExercise(deck))
            }
            is SetupDeckMenuItemClicked -> {
                actionSender.send(NavigateToDeckSettings(event.deckId))
            }
            is DeleteDeckMenuItemClicked -> {
                val numberOfDeletedDecks = repository.deleteDeckCreatingBackup(event.deckId)
                if (numberOfDeletedDecks == 1) {
                    actionSender.send(ShowDeckIsDeletedSnackbar)
                }
            }
            DeckIsDeletedSnackbarCancelActionClicked -> {
                repository.restoreLastDeletedDeck()
            }
            is SearchTextChanged -> {
                searchText.value = event.searchText
            }
            SortByMenuItemClicked -> {
                actionSender.send(ShowDeckSortingBottomSheet)
            }
            SortByNameTextViewClicked -> {
                deckSorting.value = BY_NAME
                actionSender.send(DismissDeckSortingBottomSheet)
            }
            SortByTimeCreatedTextViewClicked -> {
                deckSorting.value = BY_TIME_CREATED
                actionSender.send(DismissDeckSortingBottomSheet)
            }
            SortByLastOpenedTextViewClicked -> {
                deckSorting.value = BY_LAST_OPENED
                actionSender.send(DismissDeckSortingBottomSheet)
            }
        }
    }

}