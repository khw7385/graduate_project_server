const svg = d3.select("#graph");
const tooltip = d3.select(".tooltip")

let width = 800;
let height = 600;

const nodes= [];
const links = [];

nodes.push({
    id: graphData.target.index === 0 ? 'qep': `aqp${graphData.target.index}`,
    group: 0,
    value: graphData.target.root
});

graphData.others.forEach((other) => {
    nodes.push({
       id: other.index === 0 ? 'qep' : `aqp${other.index}`,
       group: other.editDifference,
       value: other.root
    });

    links.push({
        source: graphData.target.index === 0 ? 'qep': `aqp${graphData.target.index}`,
        target: other.index === 0 ? 'qep' : `aqp${other.index}`,
        distance: 20 * Math.pow(other.editDifference, 2)
    });
})

links.forEach(link => {
    const sourceNode = nodes.find(node => node.id === link.source);
    const targetNode = nodes.find(node => node.id === link.target);

    if (!sourceNode) {
        console.error(`Source node not found: ${link.source}`);
    }
    if (!targetNode) {
        console.error(`Target node not found: ${link.target}`);
    }
});


// 힘 시뮬레이션 생성
const simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id(d => d.id).distance(d => d.distance))
    .force("charge", d3.forceManyBody().strength(-300))
    .force("center", d3.forceCenter(width / 2, height / 2)); // 중심점 설정


// 노드 및 링크 추가
const link = svg.append("g")
    .selectAll("line")
    .data(links)
    .enter()
    .append("line")
    .attr("stroke", "#999")
    .attr("stroke-width", 2);

const node = svg.append("g")
    .selectAll("g")
    .data(nodes)
    .enter()
    .append("g")
    .attr("class", "node");



node.append("circle")
    .attr("r", 10)
    .attr("fill", d => {
        if(d.group <= 9){
            return d3.schemeSet1[d.group];
        }else{
            return d3.schemeSet2[d.group % 10];
        }
    })
    .on("mouseover", function(event, d){
        tooltip.transition()
            .duration(20)
            .style("opacity", .9);

        const tree = buildTree(d.value);

        tooltip.html(`<div class="tree">${tree}</div>`)
            .style("left", (event.pageX + 5) + "px")
            .style("top", (event.pageY - 28) + "px")

        adjustTreeLines();
    })
    .on("mouseout", () => {
        tooltip.transition()
            .duration(500)
            .style("opacity", 0);
    });

node.append("text")
    .text(d => d.id)
    .attr("dy", -15)
    .attr("text-anchor", "middle");

// 시뮬레이션 업데이트
simulation.nodes(nodes).on("tick", () => {
    link
        .attr("x1", d => d.source.x)
        .attr("y1", d => d.source.y)
        .attr("x2", d => d.target.x)
        .attr("y2", d => d.target.y);

    node.attr("transform", d => `translate(${d.x}, ${d.y})`);
});



simulation.force("link").links(links);

// 드래그 및 확대/축소 기능 구현
let isDragging = false;
let startClientX, startClientY;
let startViewBoxX = 0, startViewBoxY = 0;
let zoomLevel = 1;

const container = document.querySelector(".graph-container");

const targetQueryPlan = document.querySelector(".target-query-plan");

const targetTree = buildTree(graphData.target.root);
targetQueryPlan.innerHTML = `<div class='tree'>${targetTree}</div>`;



svg.on('mousedown', (event) => {
    isDragging = true;

    startClientX = event.clientX;
    startClientY = event.clientY;

    const viewBox = svg.attr('viewBox').split(' ').map(Number);

    startViewBoxX = viewBox[0];
    startViewBoxY = viewBox[1];

    container.style.cursor = 'grabbing';
});

svg.on('mousemove', (event) => {
    if (isDragging) {
        const deltaX = event.clientX - startClientX;
        const deltaY = event.clientY - startClientY;

        const newX = startViewBoxX - deltaX / zoomLevel; // 줌 레벨에 따라 조정
        const newY = startViewBoxY - deltaY / zoomLevel;

        svg.attr('viewBox', `${newX} ${newY} ${width * zoomLevel} ${height * zoomLevel}`);
    }
});

svg.on('mouseup', () => {
    isDragging = false;

    container.style.cursor = 'grab';
});

// 휠 이벤트로 확대/축소 구현
svg.on('wheel', (event) => {
    event.preventDefault(); // 기본 스크롤 방지

    console.log(event.deltaY);

    const delta = event.deltaY > 0 ? 0.1 : -0.1; // 휠 방향에 따라 줌 조정
    zoomLevel += delta;

    if (zoomLevel < 0.1) zoomLevel = 0.1; // 최소 줌 레벨 제한


    svg.attr('viewBox', `${startViewBoxX} ${startViewBoxY} ${width * zoomLevel} ${height * zoomLevel}`);
});