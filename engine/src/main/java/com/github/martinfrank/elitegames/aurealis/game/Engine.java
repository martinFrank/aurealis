package com.github.martinfrank.elitegames.aurealis.game;

import com.github.martinfrank.elitegames.aurealis.adventure.Adventure;
import com.github.martinfrank.elitegames.aurealis.adventure.Chapter;
import com.github.martinfrank.elitegames.aurealis.agent.ActionResult;
import com.github.martinfrank.elitegames.aurealis.agent.GameContext;
import com.github.martinfrank.elitegames.aurealis.agent.InputInterpreter;
import com.github.martinfrank.elitegames.aurealis.agent.Intent;
import com.github.martinfrank.elitegames.aurealis.agent.LlmProvider;
import com.github.martinfrank.elitegames.aurealis.agent.ResponseGenerator;
import com.github.martinfrank.elitegames.aurealis.party.Party;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Engine {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private final Session session;
    private final InputInterpreter inputInterpreter;
    private final ContextResolver contextResolver;
    private final ActionStage actionStage;
    private final ResponseGenerator responseGenerator;

    public Engine(Adventure adventure, Party party) {
        session = new Session(adventure, party);
        inputInterpreter = new InputInterpreter(LlmProvider.defaultChatModel());
        contextResolver = new ContextResolver();
        actionStage = new ActionStage();
        responseGenerator = new ResponseGenerator(LlmProvider.defaultChatModel());
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

        GameContext context = contextResolver.resolve(session);
        Intent intent = inputInterpreter.interpret(playerMessage, context);
        session.chat.add(Chat.Role.PLAYER, playerMessage);
        ActionResult result = actionStage.execute(intent, session);
        String response = responseGenerator.generate(context, intent, result);

        session.chat.add(Chat.Role.GAME_MASTER, response);
        System.out.println(response);
    }

    private void startChapter() {
        Chapter chapter = session.getCurrentChapter();
        if(chapter.startCutScene() != null){
            session.chat.add(Chat.Role.CUT_SCENE, chapter.startCutScene().text());
        }
    }
}
