document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.nav-link[data-bs-toggle="collapse"]').forEach(function (link) {
        const icon = link.querySelector('.chevron-icon'); // Collapse 아이콘 선택

        // link.addEventListener('click', function () {
        //     const isCollapsed = link.classList.contains('collapsed');
        //     console.log(isCollapsed);
        //     icon.style.transform = isCollapsed ? 'rotate(0deg)' : 'rotate(180deg)'; // 아이콘 회전
        // });

        link.addEventListener('click', function (e){
            e.preventDefault();

            const isCollapsed= link.classList.contains('collapsed');

            if(isCollapsed){
                const targetId = link.href.split('#')[1];
                const targetCollapse = document.getElementById(targetId);

                if(targetCollapse){
                    const nestedLinks = targetCollapse.querySelectorAll('.nav-link[data-bs-toggle="collapse"]');
                    nestedLinks.forEach(function (nestedLink) {
                        const nestedIcon = nestedLink.querySelector('.chevron-icon'); // Collapse 아이콘 선택
                        // 중첩된 모든 collapse를 닫음
                        const nestedCollapseId = nestedLink.href.split('#')[1];
                        const nestedCollapse = document.getElementById(nestedCollapseId);
                        if (nestedCollapse) {
                            nestedCollapse.classList.remove('show'); // collapse 상태로 만들기
                            nestedIcon.style.transform = 'rotate(0deg)';
                        }
                    });
                }

                icon.style.transform = 'rotate(0deg)';
            }else{
                icon.style.transform = 'rotate(180deg)';
            }
        })
    });
});

document.addEventListener("DOMContentLoaded", function(){
    document.querySelectorAll("aside a[data-dynamic='true']").forEach(function (link){
        link.addEventListener("click", function (e){
            e.preventDefault();
            const url = this.href;

            fetch(url)
                .then(response => response.text())
                .then(html=>{
                    document.querySelector("main").innerHTML = html;
                })
                .catch(error => console.log("Error loading connect:", error));
        })
    });

    const bTipsRadio =  document.getElementById("b-tips-radio");
    const iTipsRadio = document.getElementById("i-tips-radio");
    const bTipsInput = document.getElementById("b-tips-input");

    bTipsInput.style.display = bTipsRadio.checked ? 'block' : 'none';
    bTipsRadio.addEventListener('change', function(){
        if(this.checked){
            bTipsInput.style.display = 'block';
        }
    })

    iTipsRadio.addEventListener('change', function(){
        if(this.checked){
            bTipsInput.style.display = 'none';
        }
    })

    const switchCompareTree = document.getElementById("switch-compare-qep");
    const switchGraphTree = document.getElementById("switch-visualize-others");

    // 이벤트 리스너 추가
    switchCompareTree.addEventListener("change", () => {
        if (switchCompareTree.checked) {
            switchGraphTree.checked = false; // 다른 스위치 끄기
        }
    });

    switchGraphTree.addEventListener("change", () => {
        if (switchGraphTree.checked) {
            switchCompareTree.checked = false; // 다른 스위치 끄기
        }
    });
});

