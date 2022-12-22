const Book = require("../models/Book");
const SubCategory = require("../models/SubCategory");
const Category = require("../models/Category");
const CategoryRelations = require("../models/CategoryRelations");
const Constants = require("../constants/Constants");

const uploadBook = async (req, response) => {
    const bookInput = {
      name: req.body.name,
      author: req.body.author,
      description: req.body.description,
      price: req.body.price,
      quantity: req.body.quantity,
      publishedAt: req.body.publishedAt,
      createdAt: Date.now(),
      category: req.body.category,
      subCategory: req.body.subCategory,
      customer: req.customer.customerId
    };

    const book = await Book.create(bookInput);

    return response.json({
      message: "",
      error: false,
      data: [book]
    });
};

const updateBook = async (req, response) => {
    const books = await Book.find({
      _id: req.body._id
    });

    if (books.length == 0) {
      return response.json({
        message: "Error",
        error: true,
        data: []
      });
    }

    let bookInput = req.body;

    delete bookInput._id;

    const book = await Book.findOneAndUpdate({_id: books[0]._id}, {$set: bookInput}, {new: true});

    return response.json({
      message: "",
      error: false,
      data: [book]
    });
};

const deleteBook = (req, response) => {
    Book.deleteOne({_id: req.params.id}, (error, result) => {
        if (error) {
          return response.json({
            message: "Error",
            error: true,
            data: []
          });
        }

        return response.json({
          message: "",
          error: false,
          data: []
        });
    });
    
};


const getProducts = async (req, response) => {
  const input = req.query;

  let products = await Book.find({}).populate("subCategory").populate("category");

  if (input.subCategory === undefined && input.category === undefined) {
    return response.json({
      message: "",
      error: false,
      data: products
    });
  }

  let filteredProducts = [];

  
  
  let categories = [];
  if (input.category !== undefined) {
    if (typeof(input.category) === "string") {
      categories.push(input.category);
    } else if (Array.isArray(input.category)) {
      categories = categories.concat(input.category);
    }
  }

  let subCategories = [];
  if (input.subCategory !== undefined) {
    if (typeof(input.subCategory) === "string") {
      subCategories.push(input.subCategory);
    } else if (Array.isArray(input.subCategory)) {
      subCategories = subCategories.concat(input.category);
    }
  }

  if (categories.length !== 0 && subCategories.length !== 0) {
    for (let i = 0; i < products.length; ++i) {
      let product = products[i];

      if (categories.includes(product.category.name)) {
        if (subCategories.includes(product.subCategory.name)) {
          filteredProducts.push(product);
        }
      }
    }
  } else if (categories.length !== 0) {
    for (let i = 0; i < products.length; ++i) {
      let product = products[i];

      if (categories.includes(product.category.name)) {
        filteredProducts.push(product);
      }
    }
  } else if (subCategories.length !== 0) {
    for (let i = 0; i < products.length; ++i) {
      let product = products[i];

      if (subCategories.includes(product.subCategory.name)) {
        filteredProducts.push(product);
      }
    }
  }

  return response.json({
    message: "",
    error: false,
    data: filteredProducts
  });
};

const getProduct = (req, response) => {
  const productId = req.params.productId;
  Book.find({_id: productId}, (error, books) => {
    if (error) {
      return response.json({
        message: "Error",
        error: true,
        data: []
      });
    }

    response.json({
      message: "",
      error: false,
      data: books
    });
  });
};

module.exports = {
  uploadBook,
  updateBook,
  deleteBook,
  getProducts,
  getProduct
};