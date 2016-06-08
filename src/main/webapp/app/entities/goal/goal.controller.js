(function() {
    'use strict';

    angular
        .module('jHealthApp')
        .controller('GoalController', GoalController);

    GoalController.$inject = ['$scope', '$state', 'Goal', 'GoalSearch'];

    function GoalController ($scope, $state, Goal, GoalSearch) {
        var vm = this;
        
        vm.goals = [];
        vm.search = search;

        loadAll();

        function loadAll() {
            Goal.query(function(result) {
                vm.goals = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            GoalSearch.query({query: vm.searchQuery}, function(result) {
                vm.goals = result;
            });
        }    }
})();
