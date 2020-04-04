package com.odnovolov.forgetmenot.persistence.shortterm

import com.odnovolov.forgetmenot.domain.entity.Deck
import com.odnovolov.forgetmenot.domain.entity.GlobalState
import com.odnovolov.forgetmenot.domain.interactor.decksettings.DeckSettings
import com.odnovolov.forgetmenot.persistence.shortterm.DeckSettingsStateProvider.SerializableDeckSettingsState
import kotlinx.serialization.Serializable

class DeckSettingsStateProvider(private val globalState: GlobalState) :
    BaseSerializableStateProvider<DeckSettings.State, SerializableDeckSettingsState>() {
    @Serializable
    data class SerializableDeckSettingsState(
        val deckId: Long
    )

    override val serializer = SerializableDeckSettingsState.serializer()
    override val serializableId = SerializableDeckSettingsState::class.simpleName!!

    override fun toSerializable(state: DeckSettings.State) =
        SerializableDeckSettingsState(state.deck.id)

    override fun toOriginal(serializableState: SerializableDeckSettingsState): DeckSettings.State {
        val deck: Deck = globalState.decks.find { it.id == serializableState.deckId }!!
        return DeckSettings.State(deck)
    }
}