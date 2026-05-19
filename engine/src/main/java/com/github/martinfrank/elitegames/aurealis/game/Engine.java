package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.agent.InputInterpreterAgent;
import com.github.martinfrank.elitegames.aurealis.agent.LlmProvider;
import com.github.martinfrank.elitegames.aurealis.party.Party;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Engine {

    private final Session session;
    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private final InputInterpreterAgent inputInterpreterAgent;

    public Engine(Adventure adventure, Party party) {
        session = new Session(adventure, party);
        inputInterpreterAgent = new InputInterpreterAgent(LlmProvider.defaultChatModel());
    }

    public void start(){
        session.init();
        //ggf. noch titel usw vorstellen
        startChapter();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input  = scanner.nextLine();
            if(input.equalsIgnoreCase("exit")){
                break;
            }
            handleTurn(input);
        }

    }

    public void handleTurn(String playerMessage){
        LOG.info("handle player message: {}", playerMessage);

    }

    private void startChapter() {
        Chapter chapter = session.getCurrentChapter();
        if(chapter.startCutScene() != null){
            session.chat.add(Chat.Role.CUT_SCENE, chapter.startCutScene().text());
        }
    }
}
