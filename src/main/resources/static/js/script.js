/**
 *
 */

console.log("This is yout script File");
//alert("Working")

const toggleSidebar = () => {
  if ($(".sidebar").is(":visible")) {
    //true/band karna hai
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%");
  } else {
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%");
  }
};

const search = () => {
  //   console.log("Searching");
  let query = $("#search-input").val();

  if (query == "") {
    $(".search-result").hide();
  } else {
    //search
    console.log(query);

    //sending request to server
    let url = `/search/${query}`;
    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        //data
        // console.log(data);
        let text = `<div class="list-group">`;

        data.forEach((contacts) => {
          //   console.log(contacts);
          text += `<a href='/user/${contacts.cid}/contact' class="list-group-item list-group-item-action "> ${contacts.name}  </a>`;
        });

        text += `</div>`;
        $(".search-result").html(text);
        $(".search-result").show();
      });
  }
};
