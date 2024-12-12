document.addEventListener("DOMContentLoaded", () => {
    const containers = document.querySelectorAll(".tree-container");
    const contents = document.querySelectorAll(".tree-content");

    let scale = 1;
    let offsetX = 0;
    let offsetY = 0;
    let isDragging = false;
    let startX, startY;

    containers.forEach((container, index) => {
        const content = contents[index];

        // const tree = content.querySelector('.tree');
        // const treeElements = tree.querySelectorAll("li, a, span, ul");

        container.addEventListener("wheel", (event) => {
            event.preventDefault();
            const zoomSpeed = 0.1;
            const delta = Math.sign(event.deltaY);
            scale = Math.max(0.5, Math.min(3, scale - delta * zoomSpeed));
            content.style.transform = `translate(${offsetX}px, ${offsetY}px) scale(${scale})`;
        });

        // container.addEventListener("wheel", (event) => {
        //     event.preventDefault();
        //     const zoomSpeed = 0.1;
        //     const delta = Math.sign(event.deltaY);
        //     scale = Math.max(0.1, Math.min(2, scale - delta * zoomSpeed));
        //
        //     treeElements.forEach(element => {
        //         element.style.transform = `scale(${scale})`;
        //         element.style.transformOrigin = 'top left';
        //     });
        // })

        container.addEventListener("mousedown", (event) => {
            isDragging = true;
            startX = event.clientX - offsetX;
            startY = event.clientY - offsetY;
            container.style.cursor = "grabbing";
        });

        container.addEventListener("mousemove", (event) => {
            if (!isDragging) return;
            offsetX = event.clientX - startX;
            offsetY = event.clientY - startY;
            content.style.transform = `translate(${offsetX}px, ${offsetY}px) scale(${scale})`;
        });

        container.addEventListener("mouseup", () => {
            isDragging = false;
            container.style.cursor = "grab";
        });

        container.addEventListener("mouseleave", () => {
            isDragging = false;
            container.style.cursor = "grab";
        });
    })

})

function processTreeData (data, content) {
    const treeCanvas = document.createElement('div');
    treeCanvas.className = 'tree';

    const treeCode = buildTree(data.root);
    treeCanvas.innerHTML = treeCode;
    content.appendChild(treeCanvas);
    adjustTreeLines();
}

function buildTree (obj) {
    let treeString =
        "<li><a href='#' data-relName='" + obj.relName + "' data-operation='" + obj.operation + "'><span class='operation'>"
        + obj.operation +
        "</span><span class='cost'>"
        + obj.cost + "</span>" + "</a>";

    if(obj.children.length > 0){
        treeString += "<ul>"

        for(const idx in obj.children){
            treeString += buildTree(obj.children[idx]);
        }
        treeString += "</ul>";
    }else{
        treeString +=
            "<ul><li><a href='#'>" + obj.relName + "</a></li></ul>";
    }
    treeString += "</li>"
    return treeString
}

function adjustTreeLines(){
    const uls = document.querySelectorAll('.tree ul');

    uls.forEach(ul => {
        const lis = ul.querySelectorAll(':scope > li');

        if (lis.length >= 2) {
            const firstLi = lis[0];
            const secondLi = lis[1];

            const firstLiWidth = firstLi.offsetWidth;
            const secondLiWidth = secondLi.offsetWidth;

            const middle = firstLiWidth / 2 + (firstLiWidth / 2 + secondLiWidth / 2) / 2;

            const firstAnchor = ul.previousElementSibling;

            if(firstAnchor){
                firstAnchor.style.position = 'relative';
                const ulWidth = ul.offsetWidth;
                const moveLeft = ulWidth / 2 - middle;
                if(moveLeft > 0){
                    firstAnchor.style.left = `-${moveLeft}px`;
                }else{
                    firstAnchor.style.right = `${moveLeft}px`;
                }
            }

            ul.style.setProperty('--middle-position',`${middle}px`)
        }else if(lis.length === 1){
            ul.style.setProperty('--middle-position', '50%');

            ul.style.display = 'flex';
            ul.style.justifyContent = 'center';
        }
    })
}