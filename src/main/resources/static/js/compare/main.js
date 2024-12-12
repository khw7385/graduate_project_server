document.addEventListener("DOMContentLoaded", () => {
    const treeContainers = document.querySelectorAll(".tree-container");
    const treeContents = document.querySelectorAll(".tree-content");
    const treeData = [qep, aqp];

    treeContainers.forEach((treeContainer, index) => {
        const treeContent = treeContents[index];
        const data = treeData[index];

        treeContainer.style.display = "flex";

        processTreeData(data, treeContent);
    })

    console.log(compareData);
    console.log(qep);
    console.log(aqp);

    const trees = document.querySelectorAll(".tree");
    const colors = ['green', 'purple',  'orange', 'yellow'];


    for (let i = 0; i < compareData.sameJoinTrees.length; i++) {
        trees.forEach((tree, index) => {
            const li = tree.querySelector("li");

            compareRelNameForColoringSameJoinSubtree(compareData.sameJoinTrees[i], li, colors[i]);
        })
    }

    compareRelNameForColoringDifferentOperation(compareData.relationOperations2, trees[0], 'blue');
    compareRelNameForColoringDifferentOperation(compareData.relationOperations1, trees[1], 'red');

})

function compareRelNameForColoringSameJoinSubtree(value, tag, color){
    const aTag = tag.querySelector("a");
    const relName = aTag.getAttribute('data-relname');

    if(relName === value){
        colorJoinSubtree(tag, color);
    }else{
        const lis = tag.querySelectorAll("ul > li");

        lis.forEach(li => compareRelNameForColoringSameJoinSubtree(value, li, color));
    }
}

const joinOperation = ["NestLoop", "NestLoopParam", "MergeJoin", "HashJoin"];

// function parseJoinOperation(data){
//     const operations = [];
//     Object.entries(data).forEach(([key ,value]) => {
//         if(key.includes("*")){
//             const rawTables = value.split("*");
//             const sortedTables = [...rawTables].sort();
//
//             const order = rawTables.map(table => sortedTables.indexOf(table) + 1);
//
//             const operation = {
//                 tables: rawTables,
//                 order: order,
//                 operation: value
//             };
//             operations.push(operation);
//         }
//     });
//     return operations;
// }
//
// function parseScanOperation(data){
//     const operations = [];
//     Object.entries(data).forEach(([key, value]) => {
//         if(!key.includes("*")){
//             const operation = {
//                 table: key,
//                 operation: value
//             };
//
//             operations.push(operation);
//         }
//     })
//     return operations;
// }

// function compareOperation(operations1, operations2){
//     const diffs = [];
//
//     operations1.forEach(op1 => {
//         operations2.forEach(op2 => {
//             if(JSON.stringify(op1.table) === JSON.stringify(op2.table)){
//                 if(JSON.stringify(op1.order) !== JSON.stringify(op2.order)){
//                 }
//             }
//         })
//     })
// }

const joinOperations = ['NestedLoop', 'NestedLoopParam', 'HashJoin', 'MergeJoin'];

function compareRelNameForColoringDifferentOperation(value, tag, color){
    const aTag = tag.querySelector("a");
    const relName = aTag.getAttribute('data-relname');
    const operation = aTag.getAttribute('data-operation');

    if(relName !== null) {
        if(joinOperation.includes(operation) || operation.endsWith('Scan')) {
            if (value[relName] !== operation || value[relName] === undefined) {
                aTag.style.color = color;
            }
        }
        const lis = tag.querySelectorAll("ul > li");
        lis.forEach(li => compareRelNameForColoringDifferentOperation(value, li, color));
    }
}


function colorJoinSubtree(tag, color){
    const aTag = tag.querySelector("a");
    aTag.style.borderColor = color;
    aTag.style.borderWidth = '2px';
    aTag.style.transition = 'border-color 0.3s ease';

    const ul = tag.querySelector("ul");

    if(ul){
        ul.classList.add('same-subtree');
        ul.style.setProperty('--border-color', color);
    }

    const lis = tag.querySelectorAll("ul > li");

    lis.forEach(li => {
        if(li){
            li.classList.add('same-subtree');
            li.style.setProperty('--border-color', color);
        }
        colorJoinSubtree(li, color)
    });
}
