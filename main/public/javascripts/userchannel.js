
var UserChannel = function (spec) {
  spec.channel_name = spec.user.user_id + "_channel";
  var that = Channel(spec),
    my = {};
  my.types = {
    NEW_POWER: "newpower",
    KARMA: "karma"
  };

  that.bindNewKarmaKube = function (f) {
    that.bind(my.types.KARMA, f);
  };  
  
  that.bindNewPower = function (f) {
    that.bind(my.types.NEW_POWER, f);
  };
  
  return that;
};