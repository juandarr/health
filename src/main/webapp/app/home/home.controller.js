(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'Points', 'LoginService', '$state'];

    function HomeController ($scope, Principal, Points, LoginService, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.pointsThisWeek = 0;
        vm.pointsPercentage = 0;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
            Points.thisWeek(function(data) {
                vm.pointsThisWeek = data;
                vm.pointsPercentage = (data.points / 98) * 100;
            });
        }


        function register () {
            $state.go('register');
        }
    }
})();
