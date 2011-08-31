


describe("trivia api", function () {
  var API = TriviaAPI(1);
    
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
      API.getBatch("Music", function (batch) {
        expect(batch.questions.length).toEqual(10);
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
      API.input("blahblahblah", function (question) {
        expect(false).toBeTruthy(); // should never happen
      });
      API.input("yes", function (question) {
        nextQuestion = question;
      });
      waitsFor(function () {
        return nextQuestion;
      }, "to get batch of questions", 3000);  
    }, 1);
        
        
    sequentialIf("should respond with the right type of response", function () {
      var rightAnswer = -1,
        wrongAnswer = -1,
        checkpoint = 0;
        
      API.input("notananswer", function (response) {
         expect(response.type).toEqual("REPEAT");
         checkpoint++;
      });
      $.each(nextQuestion.answers, function (k, a) {
        if (a.isCorrect) {
          rightAnswer = k;
        } else {
          wrongAnswer = k;
        }
      });
      expect(rightAnswer).toBeGreaterThan(-1);
      expect(wrongAnswer).toBeGreaterThan(-1);
      
      API.input(['a', 'b', 'c', 'd'][wrongAnswer], function (response) {
        expect(response.type).toEqual("INCORRECT");
        checkpoint++;
      });
      API.input(['a', 'b', 'c', 'd'][rightAnswer], function (response) {
        expect(response.type).toEqual("CORRECT");
        checkpoint++;
      });      
      waitsFor(function () {
        return checkpoint == 3;
      }, "got all responses we needed", 3000);  
    }, 2);        
  });
  
});
