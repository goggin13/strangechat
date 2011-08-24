
var User = function (spec) {
  var that = {};
  that.user_id = spec.user_id;
  that.alias = spec.alias;
  that.session = spec.session;
  that.avatar = spec.avatar;
  return that;
};