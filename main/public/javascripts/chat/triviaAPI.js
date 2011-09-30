/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: false, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, Event, types, console, HTTP, RoomChannel, MatchMaker, APusher, Pusher, UserChannel, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */

var TriviaAPI = function (spec) {
  "use strict";
  var my = {},
    that = {};
  my.user_id = spec.user_id;
  my.home_url = spec.home_url;
  my.home_url += "trivia/";
  
  my.batch = null;
  my.startWord = false;
  my.question_index = 0;
  my.correct = 0;
    
  my.states = {
    PROCESSING: {
      f: function () { },
      name: "processing"
    }
  };
  
  // Welcome
  my.welcomeInput = function (text, callback) {
    var welcome = my.randomFrom(my.batch.salutationResponses),
      welcomeText = welcome.text + "  When you are ready, just type \"" + my.startWord + "\" to begin";    
    my.state = my.states.WAITING;
    callback({type: "SALUTATION", text: welcomeText});
  };
  my.states.WELCOME = {
    f: my.welcomeInput,
    name: "welcome"
  };
    
  // Waiting for user input
  my.waitingInput = function (text, callback) {
    if (text.toLowerCase().replace(/[\W]/g, "") === my.startWord.toLowerCase().replace(/[\W]/g, "")) {
      my.state = my.states.QUESTIONING;
      callback(my.currentQuestion());
    } else {
      callback({
        leave: "1", 
        text: "You aren't following simple commands. Come back when you will treat me right."
      });
      my.state = my.states.WELCOME;
    }
  };
  my.states.WAITING  = {
    f: my.waitingInput,
    name: "waiting"
  };
    
  // Asking questions
  my.questionInput = function (text, callback) {
    text = text.toLowerCase();
    if (!my.validateAnswer(text)) {
      callback(my.randomRepeatResponse());
      return;
    } 
    my.state = my.states.PROCESSING;
    var url = my.home_url + 'answerQuestion',
      answer = my.currentQuestion().answers[['a', 'b', 'c', 'd'].indexOf(text)],
      data = {
        qid: my.currentQuestion().id, 
        user_id: my.user_id,
        aid: answer.id
      };      
    HTTP.send(url, "GET", data);
    
    if (answer.isCorrect) {
      my.correct++;
      callback(my.randomCorrectResponse());
      setTimeout(function () {
        my.incrementAndGetQuestion(callback);
      }, 500);
    } else {
      setTimeout(function () {
        callback({
          incorrectRetort: my.randomIncorrectResponse(),
          correctAnswer: my.currentCorrectResponse()
        });
        setTimeout(function () {
          my.incrementAndGetQuestion(callback);
        }, 500);
      }, 500);      
    }
  };
  my.states.QUESTIONING = {
    f: my.questionInput,
    name: "questioning"
  };
  
  // Completed round
  my.finishedRoundInput = function (text, callback) {
    callback({
      total: my.batch.questions.length,
      correct: my.correct
    });
    my.reportRound(my.batch.questions.length, my.correct);
    that.startBatch(my.batch.category.name, function () {
      callback({
        type: "continue",
        text: "To start another round, just type \"" + my.startWord + "\""    
      });
      my.correct = 0;
      my.state = my.states.WAITING;      
    });
  };
  my.states.FINISHED_ROUND = {
    f: my.finishedRoundInput,
    name: "finished_round"
  };
    
  // Completed Category
  my.finishedInput = function (input, callback) {
    var text = "You have completed all the questions from this category!  ";
    text += "Head back to the [[Trivia Page]][[" + oApp.base_url + "trivia]] to find some more competition!";
    callback({type: "completed", text: text});
  };  
  my.states.FINISHED = {
    f: my.finishedInput,
    name: "finishedinput"
  };
      
  that.input = function (text, callback) {
    my.state.f(text, callback);
  };
    
  /**
   * HELPER FUNCTIONS 
   **/
  my.randomFrom = function (col) {
    var i = Math.floor(Math.random() * (col.length - 1));
    return col[i];
  };
  
  my.randomRepeatResponse = function () { return my.randomFrom(my.batch.repeatResponses); };
  my.randomCorrectResponse = function () { return my.randomFrom(my.batch.correctResponses); };
  my.randomIncorrectResponse = function () { return my.randomFrom(my.batch.incorrectResponses); };
        
  my.validateAnswer = function (text) {
    return text.length === 1 && 'abcd'.indexOf(text) > -1;
  };
  
  my.incrementAndGetQuestion = function (callback) {
    my.question_index++;
    if (my.question_index >= my.batch.questions.length) {
      my.categoryIsComplete(my.batch.category.name, function (isComplete) {
        if (isComplete) {
          my.state = my.states.FINISHED;
        } else {
          my.state = my.states.FINISHED_ROUND;
        }
        my.state.f("", callback); 
      });
    } else {
      callback(my.currentQuestion());
      my.state = my.states.QUESTIONING;
    }
  };

  my.currentCorrectResponse = function () {
    var question = my.currentQuestion(),
      correct = false;
    $.each(question.answers, function (k, answer) {
      if (answer.isCorrect) {
        correct = answer;
      }
    });
    return correct;
  };
  
  my.currentQuestion = function () {
    return my.batch.questions[my.question_index];
  };
  
  my.reportRound = function (total, correct) {
    var url = my.home_url + "reportRound",
      data = {
        user_id: my.user_id,
        total: total,
        correct: correct
      };
    HTTP.send(url, "GET", data);
  };
  
  my.categoryIsComplete = function (category, callback) {
    that.getAvailableCategories(function (categories) {
      callback(!categories.hasOwnProperty(category));
    });
  };
  
  that.getAvailableCategories = function (callback) {
    var url = my.home_url + "getEligibleTrivia",
      data = {
        user_id: my.user_id
      };
    HTTP.send(url, "GET", data, callback);    
  };
  
  that.startBatch = function (category_unformatted, callback) {
    var url = my.home_url + "getBatch", meta,
      category = MyUtil.toTitleCase(category_unformatted),
      data = {
        user_id: my.user_id,
        name: category
      };
    HTTP.send(url, "GET", data, function (batch) {
      if (batch.questions.length !== 0) {
        my.state = my.states.WELCOME;
      } else {
        my.state = my.states.FINISHED;
      }
      my.question_index = 0;
      my.batch = batch;
      meta = TriviaBots[my.batch.category.name];      
      my.startWord = meta.startWord;
      if (callback) {
        callback(User({
          user_id: -4,
          alias: meta.alias,
          session: "N/A",
          avatar: meta.avatar    
        }), batch);
      }
    });
  };
  
  my.init = function () {
    return that;
  };
  
  return my.init();
};