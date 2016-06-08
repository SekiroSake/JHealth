(function() {
    'use strict';

    angular
        .module('jHealthApp')
        .controller('GoalDeleteController',GoalDeleteController);

    GoalDeleteController.$inject = ['$uibModalInstance', 'entity', 'Goal'];

    function GoalDeleteController($uibModalInstance, entity, Goal) {
        var vm = this;

        vm.goal = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Goal.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
