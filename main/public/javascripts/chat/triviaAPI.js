/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
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
  
  my.triviaBots = {
    Music: {
      startWord: "go",
      avatar: "http://bnter.com/web/assets/images/4571__w320_h320.png"
    },
    General: {
      startWord: "yes",
      avatar: "http://bnter.com/web/assets/images/4571__w320_h320.png"
    },                                                                
    Movies: {                                                         
      startWord: "movies",                                            
      avatar: "http://bnter.com/web/assets/images/4571__w320_h320.png"
    },                                                                
    Books: {                                                          
      startWord: "books",                                             
      avatar: "http://bnter.com/web/assets/images/4571__w320_h320.png"
    },                                                                
    Tech: {                                                           
      startWord: "tech",                                              
      avatar: "http://bnter.com/web/assets/images/4571__w320_h320.png"
    },                                                                
    Cats: {                                                           
      startWord: "meow",                                              
      avatar: "http://bnter.com/web/assets/images/4571__w320_h320.png"
    }        
  };
  
  my.waitingInput = function (text, callback) {
    if (text.toLowerCase() == my.startWord.toLowerCase()) {
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
  
  my.finishedInput = function (text, callback) {
    
  };
  
  my.randomFrom = function (col) {
    var i = Math.floor(Math.random() * (col.length - 1));
    return col[i];
  };
  
  my.randomRepeatResponse = function () { return my.randomFrom(my.batch.repeatResponses); };
  my.randomCorrectResponse = function () { return my.randomFrom(my.batch.correctResponses); };
  my.randomIncorrectResponse = function () { return my.randomFrom(my.batch.incorrectResponses); };
        
  my.welcomeInput = function (text, callback) {
    var welcome = my.randomFrom(my.batch.salutationResponses),
      welcomeText = welcome.text + "  When you are ready, just type \"" + my.startWord + "\" to begin";    
    my.state = my.states.WAITING;
    callback({type: "SALUTATION", text: welcomeText});
  };
        
  my.validateAnswer = function (text) {
    return text.length == 1 && 'abcd'.indexOf(text) > -1;
  };
  
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
    
  my.states = {
    WELCOME: my.welcomeInput,
    WAITING: my.waitingInput,
    QUESTIONING: my.questionInput,
    FINISHED: my.finishedInput,
    PROCESSING: function () {}
  };
  my.state = my.states.WELCOME; 

  my.incrementAndGetQuestion = function (callback) {
    my.question_index++;
    if (my.question_index >= my.batch.questions.length) {
      my.state = my.states.FINISHED;
      callback({
        total: my.batch.questions.length,
        correct: my.correct
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

  that.input = function (text, callback) {
    var f = my.state(text, callback);
  };
  
  that.getBatch = function (category_unformatted, callback) {
    var url = my.home_url + "getBatch",
      category = MyUtil.toTitleCase(category_unformatted),
      data = {
        user_id: my.user_id,
        name: category
      };
    HTTP.send(url, "GET", data, function (batch) {
      my.batch = batch;
      my.startWord = my.triviaBots[my.batch.category.name].startWord;
      if (callback) {
        callback(User({
          user_id: -4,
          alias: category,
          session: "N/A",
          avatar: my.triviaBots[category].avatar    
        }), batch);
      }
    });
  };
  
  my.init = function () {
    return that;
  };
  
  return my.init();
};