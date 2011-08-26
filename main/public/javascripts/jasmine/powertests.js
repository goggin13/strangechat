describe("powers", function () {
  
  beforeEach(function () {
    beforeEachFunction();
  });

  afterEach(function () {
    afterEachFunction();
  });
  
  describe("qualify for mind reader", function () {
    it("should occur after using 3 icebreakers", function () {
      var msg1 = false, msg2 = false;
      
      user3.api.requestRandomRoom(function (user) { msg1 = true; });
      user4.api.requestRandomRoom(function (user) { msg2 = true; });
      
      waitsFor(function () { return msg1 && msg2; }, "get matched up", 4000);
      
      runs(function () {
        var user4Data = user3.api.im_talking_to[user4.api.user_id],
          user4channel = user4Data.channel,
          power_id = user4.api.superPowers[0].id,
          gotIt = false;
          
        for (var i = 0; i < 3; i++) {
          user4channel.usePower(power_id, user4.api.user);
        }
        
        user4.api.bindNewPower(function (power) {
          if (power.superPower.name == "Mind Reader") {          
            expect(power.storedPower.id).toBeDefined();
            expect(power.superPower.name).toEqual("Mind Reader");
            expect(power.storedPower.level).toEqual(1);
            gotIt = true;
          }
        });
        
        user4.api.checkPowers();
        waitsFor(function () {
          return gotIt;
        }, "receiving mind reader", 12000);
      });
    });
  });
  
  describe("qualify for karma", function () {
    it("should occur after chatting for 100 seconds", function () {
      var msg = 0;
      
      user3.api.requestRandomRoom(function (user) { msg++; });
      user4.api.requestRandomRoom(function (user) { msg++; });
      
      waitsFor(function () { return msg == 2; }, "get matched up", 4000);
      runs(function () {
        var user4Data = user3.api.im_talking_to[user4.api.user_id],
          user3Data = user4.api.im_talking_to[user3.api.user_id],
          user3channel = user3Data.channel,
          user4channel = user4Data.channel,
          power_id = user4.api.superPowers[0].id,
          gotIt = false;
        
        user3channel.bindToIsTyping(function () {});  
        user4channel.bindToIsTyping(function () {});  
        for (var i = 0; i < 20; i++) {
          user4channel.userIsTyping(user4.api.user_id, "hello from 4");
        }
        
        var power_id;
        user4.api.bindNewPower(function (power) {
          if (power.superPower.name == "Karma") {
            expect(power.storedPower.id).toBeDefined();
            expect(power.storedPower.level).toEqual(1);
            expect(power.storedPower.available).toEqual(1); 
            power_id = power.storedPower.id;
            expect(power_id).toBeDefined();           
            gotIt = true;            
          }
        });
        
        user4.api.checkPowers();
        waitsFor(function () {
          return gotIt;
        }, "receiving some karma", 5000);
        runs(function () {
          var seeUsed = false;
          
          var karmaKubeID;
          user3channel.bindToUsedPower(function (power) {
            expect(power.used_on).toEqual(user3.api.user.user_id);
            expect(power.superPower.name).toEqual("Karma");
            expect(power.result).toMatch("KarmaKube-\\d+");
            karmaKubeID = power.result.match(/\d+/)[0];
            seeUsed = true;
          });
          
          user4channel.usePower(power_id, user4.api.user, user3.api.user, true);
          waitsFor(function () { 
            return seeUsed;
          }, "to see used karma", 4000);
          runs(function () {
            var gotResponse = false;
            user3.api.openKube(karmaKubeID, function (reward) {
              expect(reward.isGood).toBeTruthy();
              expect(reward.name).toEqual("Gold Coin");
              gotResponse = true;
            });
            waitsFor(function () {
              return gotResponse;
            }, "response from opening kube", 3000);
          });
        });
      });
    });
  });
});