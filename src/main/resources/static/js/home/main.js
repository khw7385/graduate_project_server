document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".example-sql").forEach((link) => {
        link.addEventListener("click", (event) => {
            event.preventDefault();
            document.getElementById("sql-textarea").value = link.getAttribute("data-sql");
            document.querySelector("#plan-table-container").innerHTML = null;
            fetch('/clear-userId',{
                method: 'POST'
            })
            .then(() => {
                    document.querySelector("#plan-table-container").innerHTML = null;
            })
            .catch(error => {
                console.error("Error:", error);
            })
        });
    });

    document.querySelector('#sql-textarea').addEventListener('input', function(){
        fetch('/clear-userId',{
            method: 'POST'
        })
        .then(() => {
            document.querySelector("#plan-table-container").innerHTML = null;
        })
        .catch(error => {
            console.error("Error:", error);
        })
    })

    document.querySelectorAll('input[name="flexRadio"]').forEach((radio) => {
        radio.addEventListener('change', function(){
            document.querySelector("#plan-table-container").innerHTML = null;

            fetch('/clear-userId',{
                method: 'POST'
            })
                .then(() => {
                    document.querySelector("#plan-table-container").innerHTML = null;
                })
                .catch(error => {
                    console.error("Error:", error);
                })
        })
    })

    document.getElementById('run-query').addEventListener('click', (event) => {
        event.preventDefault();
        const textarea_data = document.getElementById("sql-textarea");

        if(textarea_data.value.trim() === ""){
            const alertHtml = `
                <div class="textarea-alert alert alert-warning alert-dismissible fade show custom-alert" role="alert">
                    <svg class="bi flex-shrink-0 me-2" width="24" height="24" role="img" aria-label="Warning:"><use xlink:href="#exclamation-triangle-fill"/></svg>
                    Please input the SQL!
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            `;

            document.body.insertAdjacentHTML('beforeend', alertHtml);

            const alertElement = document.querySelector('.textarea-alert');
            alertElement.style.display = 'block';


            setTimeout(function() {
                alertElement.remove();
            }, 3000);
        }else{
            const selectedValue = document.querySelector('input[name="flexRadio"]:checked').value;

            const jsonData = {
                sql: textarea_data.value,
                selectedValue: selectedValue
            }

            const planTable = document.querySelector("#plan-table");

            if(!planTable){
                if(selectedValue === '0'){
                    jsonData.number = document.getElementById('b-tips-number').value;
                }

                fetch('/run-sql', {
                    method: 'POST',
                    headers:{
                        'Content-Type':'application/json',
                    },
                    body: JSON.stringify(jsonData)
                }).then(response => {
                    if (response.ok) {
                        return response.text()
                    } else {
                        return response.json().then(errorResponse => {
                            throw new Error(errorResponse.message);
                        })
                    }
                }).then(html=> {
                    document.querySelector("#plan-table-container").innerHTML = html;
                }).catch(error => {
                    const alertHtml = `
                    <div class="textarea-alert alert alert-danger alert-dismissible fade show custom-alert" role="alert">
                        <svg class="bi flex-shrink-0 me-2" width="24" height="24" role="img" aria-label="Warning:"><use xlink:href="#exclamation-triangle-fill"/></svg>
                            ${error.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                 `;

                    document.body.insertAdjacentHTML('beforeend', alertHtml);

                    const alertElement = document.querySelector('.textarea-alert');
                    alertElement.style.display = 'block';

                    setTimeout(function() {
                        alertElement.remove();
                    }, 3000);
                });
            }else{
                if(selectedValue === "1"){
                    fetch('/run-sql', {
                        method: 'POST',
                        headers: {
                            'Content-type':'application/json'
                        },
                        body: JSON.stringify(jsonData)
                    }).then(response => {
                        if (response.ok) {
                            return response.text();
                        } else {
                            return response.json().then(errorResponse => {
                                throw new Error(errorResponse.message);
                            })
                        }
                    })
                    .then(html => {
                        document.querySelector("#plan-table-container").innerHTML = html;
                    })
                    .catch(error => {
                        const alertHtml = `
                            <div class="textarea-alert alert alert-danger alert-dismissible fade show custom-alert" role="alert">
                                <svg class="bi flex-shrink-0 me-2" width="24" height="24" role="img" aria-label="Warning:"><use xlink:href="#exclamation-triangle-fill"/></svg>
                                    ${error.message}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                         `;

                        document.body.insertAdjacentHTML('beforeend', alertHtml);

                        const alertElement = document.querySelector('.textarea-alert');
                        alertElement.style.display = 'block';


                        setTimeout(function() {
                            alertElement.remove();
                        }, 3000);
                    })
                }
            }
        }
    })


});
function handleTrClick(tr){
   const index = tr.getAttribute("data-index");
    const compareQEPSwitch = document.querySelector('#switch-compare-qep');
    const visualizeOthersSwitch = document.querySelector('#switch-visualize-others');


    if(compareQEPSwitch.checked){
        if(index === "0") return;
        const url = `/compare-tree?index=${index}`;
        const aTag = document.createElement("a");
        aTag.href = url;
        aTag.target = "_blank";
        document.body.appendChild(aTag);
        aTag.click();
        document.body.removeChild(aTag);
    }else if(visualizeOthersSwitch.checked){
        if(index === "0") return;
        const url = `/visualize-difference?index=${index}`;
        const aTag = document.createElement("a");
        aTag.href = url;
        aTag.target = "_blank";
        document.body.appendChild(aTag);
        aTag.click();
        document.body.removeChild(aTag);
    }else{
        const url = `/one-tree?index=${index}`;
        fetch(url, {
            method: "GET",
        })
            .then(response => response.json())
            .then(data => {
                const treeContainer = document.querySelector(".tree-container");
                const treeContent = document.querySelector(".tree-content");
                treeContent.innerHTML = "";
                treeContainer.style.display = "flex";

                processTreeData(data, treeContent);
            })
            .catch(error => {
                console.error("Error:", error);
            })
    }
}

