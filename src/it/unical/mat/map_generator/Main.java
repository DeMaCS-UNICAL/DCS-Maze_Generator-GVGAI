package it.unical.mat.map_generator;

import java.util.Random;

import it.unical.mat.util.ConfigurationFile;
import tools.Utils;
import tracks.ArcadeMachine;

public class Main {

	public static void main(String[] args) throws Exception {

		ConfigurationFile configurationFile = new ConfigurationFile();
		MapGenerator map = new MapGenerator(configurationFile);
		map.generateMap();
		
//		// Available tracks:
//		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
//		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
//		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
//		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";
//
//		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
//        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
//        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
//		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";
//
//		//Load available games
//		String spGamesCollection =  "examples/all_games_sp.csv";
//		String[][] games = Utils.readGames(spGamesCollection);
//
//		//Game settings
//		boolean visuals = true;
//		int seed = new Random().nextInt();
//
//		// Game and level to play
//		int gameIdx = 100;
//		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
//		String gameName = games[gameIdx][1];
//		String game = games[gameIdx][0];
//		String level1 = "level_map/level_map";
//
//		String recordActionsFile = null;				
//		
//		// 1. This starts a game, in a level, played by a human.
//		ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
	}
}
