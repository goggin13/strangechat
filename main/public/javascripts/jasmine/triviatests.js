


describe("trivia api", function () {
  var API = TriviaAPI({
    home_url: "http://localhost:9000/",
    user_id: 1
  });
    
  describe("set up", function () {
    var step = 0,
      incStep = function () { step++; },
      sequentialIf = function (desc, f, next) {
        it(desc, function () {
          waitsFor(function () { return step == next; }, "the next step (" + next +")", 4000);
          runs(f);
          runs(incStep);
        });
      };
    
    sequentialIf("should retrieve a batch of questions", function () {
      var gotBatch = 0;
      API.startBatch("Music", function (user, batch) {
        expect(10).toEqual(batch.questions.length);
        $.each(batch.questions, function (i, q) {
          expect(q.category.name).toEqual("Music");
        });
        gotBatch = true;
      });
      waitsFor(function () {
        return gotBatch;
      }, "to get batch of questions", 3000);  
    }, 0);

    var nextQuestion;
    sequentialIf("should wait for me to say yes to start", function () {
      // first says hi
      API.input("blahblahblah", function (response) {
        expect("SALUTATION").toEqual(response.type);
      });
      // leaves if we don't respond well
      API.input("blahblahblah", function (response) {
        expect("1").toEqual(response.leave);
      });    
      // salutation again
      API.input("anything", function (response) {
        expect("SALUTATION").toEqual(response.type);
      });
      // and starts now
      API.input("Right on", function (question) {
        expect(question.text).toBeDefined();
        expect("Music").toEqual(question.category.name); 
        nextQuestion = question;
      });
      waitsFor(function () {
        return nextQuestion;
      }, "to get batch of questions", 3000);  
    }, 1);
        
    var getAnswerFromQuestion = function (isWrong) {
      return function (question) {
        var result = -1;
        $.each(question.answers, function (k, a) {
          if (a.isCorrect === isWrong) {
            result = k;
          }
        });
        return result;
      };
    };
    var getWrongAnswer = getAnswerFromQuestion(false);
    var getRightAnswer = getAnswerFromQuestion(true);
    
    sequentialIf("should respond with the right type of response", function () {
      var rightAnswer = -1,
        wrongAnswer = getWrongAnswer(nextQuestion),
        checkpoint = 0;
      
      expect(wrongAnswer).toBeGreaterThan(-1);
      API.input("notananswer", function (response) {
         expect(response.type).toEqual("REPEAT");
         checkpoint++;
      });
      
      API.input(['a', 'b', 'c', 'd'][wrongAnswer], function (response) {
        if (response.hasOwnProperty("incorrectRetort") && checkpoint === 1) {
          checkpoint++;
        } else if (response.hasOwnProperty("text") && checkpoint === 2) {
          checkpoint++;
          rightAnswer = getRightAnswer(response);
          expect(wrongAnswer).toBeGreaterThan(-1);
          
          // brief second for it to process
          setTimeout(function () {
            API.input(['a', 'b', 'c', 'd'][rightAnswer], function (response) {
              checkpoint++;
            });
          }, 100);

        } else {
          expect(false).toBeTruthy();
        }
      });
            
      waitsFor(function () {
        return checkpoint == 4;
      }, "got all responses we needed", 3000);  
    }, 2);        
  });
  
});
