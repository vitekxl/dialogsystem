package models.items.phrase

import models.Answer
import models.items.ADialogItem
import models.items.text.PhraseText
import tools.AnswersTool
import tools.ConsoleAnswerChooser
import tools.FirstPhraseChooser
import tools.SimplePhrasePrinter

abstract class APhrase  : ADialogItem, Phrase  {
    public var texts: PhraseText
        private set

    final override val id: String

    protected var count = 0
        private set;

    protected fun resetCount(){
        count = 0
    }

    public var phrasePrinter : PhrasePrinter  = SimplePhrasePrinter()
    public var phraseChooser : PhraseChooser  = FirstPhraseChooser()
    public var answerChooser : AnswerChooser  = ConsoleAnswerChooser()

    override val answers: Array<Answer>
        get() = texts.answers;

    override val phrases: Array<String>
        get() = texts.text;

    constructor(id: String, phrases: Array<String>,  answers: Array<Answer>){
        this.id = id
        this.texts = PhraseText(id, phrases, answers)
    }

    constructor(id: String, phrases: PhraseText){
        this.id = id
        this.texts = phrases
    }

    abstract override fun filter(inputAnswers: Array<Answer>, inputPhrases: Array<String>) : FilterResult


    final override fun body(): Answer {
        count ++;
        val answers = AnswersTool.copyArrayOrAnswers(this.answers)
        val phrases = this.phrases.clone()

        val bodyAnswer = filter(answers, phrases)

        val phrase = phraseChooser.choose(bodyAnswer.phrases)
        phrasePrinter.printTextDialog(phrase, bodyAnswer.answers)
        return answerChooser.chooseAnswer(bodyAnswer.answers)
    }

    override fun toString(): String {
        return "{${this.javaClass.simpleName}: id=$id, phrases=${texts}}"
    }

    data class FilterResult(val answers: Array<Answer>, val phrases: Array<String>)
}