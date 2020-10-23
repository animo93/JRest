## Introduction

JRest provides an opportunity to create an API contract via a Java Interface
```
public interface MyApiInterface {

    @REQUEST(endpoint = "/query",type = HTTP_METHOD.GET)
    @HEADERS("Content_type:application/json")
    APICall<Void,ApiResponse> testCall(@PATH(value = "function") String function,
			@PATH(value = "symbol") String symbol,
			@PATH(value = "apikey") String apiKey);
   

}
```

### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/animo93/JRest/settings). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://github.com/contact) and we’ll help you sort it out.
