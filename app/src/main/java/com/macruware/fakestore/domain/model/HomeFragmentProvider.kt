package com.macruware.fakestore.domain.model

// Esta clase es para que el HomeProductDetailFragment sepa desde dónde viene y pueda navegar hacia atrás
enum class HomeFragmentProvider {
    HomeProductListFragment,
    HomeCategoryPlpFragment,
    HomeSearchedProductFragment,
    CartDetailFragment
}