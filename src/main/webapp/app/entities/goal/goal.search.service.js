(function() {
    'use strict';

    angular
        .module('jHealthApp')
        .factory('GoalSearch', GoalSearch);

    GoalSearch.$inject = ['$resource'];

    function GoalSearch($resource) {
        var resourceUrl =  'api/_search/goals/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
