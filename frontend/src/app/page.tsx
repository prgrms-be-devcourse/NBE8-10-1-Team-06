"use client";

import { useEffect, useMemo, useState } from "react";

type Product = {
  id: string;
  name: string;
  subtitle?: string;
  category?: string;
  price: number;
  img_url?: string;
};

type MenuItem = {
  menu_id: number;
  category: string;
  menu_name: string;
  price: number;
  img_url: string;
};

type OrderHistory = {
  email: string;
  orders: Array<{
    address: string;
    postcode: number;
    items: Array<{
      menuName: string;
      menuPrice: number;
      count: number;
    }>;
  }>;
};

type CartMap = Record<string, number>;

const currency = new Intl.NumberFormat("ko-KR");
const formatPrice = (value: number) => `${currency.format(value)}원`;

export default function Home() {
  const [products, setProducts] = useState<Product[]>([]);
  const [cart, setCart] = useState<CartMap>({});
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isCheckoutLoading, setIsCheckoutLoading] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    category: "",
    menu_name: "",
    price: "",
    image: "",
  });
  const [orderForm, setOrderForm] = useState({
    email: "",
    address: "",
    postalCode: "",
  });
  const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false);
  const [historyEmail, setHistoryEmail] = useState("");
  const [orderHistory, setOrderHistory] = useState<OrderHistory | null>(null);
  const [isHistoryLoading, setIsHistoryLoading] = useState(false);

  const loadProducts = async () => {
    try {
      // 먼저 백엔드 API에서 메뉴 데이터 가져오기 시도
      const res = await fetch("/api/menu", {
        cache: "no-store",
      });

      if (res.ok) {
        const apiData: MenuItem[] = await res.json();
        // API 응답을 Product 형식으로 변환
        const convertedProducts: Product[] = apiData.map((item) => ({
          id: item.menu_id.toString(),
          name: item.menu_name,
          category: item.category,
          price: item.price,
          img_url: item.img_url,
        }));
        setProducts(convertedProducts);
      } else {
        throw new Error("백엔드 API 호출 실패");
      }
    } catch (err) {
      console.error("백엔드 API 호출 실패, 더미 데이터 사용:", err);
      // 백엔드 API 호출 실패 시 더미 데이터 사용
      try {
        const res = await fetch("/api/products", { cache: "no-store" });
        if (!res.ok) throw new Error("상품을 불러오지 못했습니다");
        const data: Product[] = await res.json();
        setProducts(data);
      } catch (fallbackErr) {
        console.error("더미 데이터 로드 실패:", fallbackErr);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const addToCart = (id: string) => {
    setCart((prev) => {
      const currentTotal = Object.values(prev).reduce(
        (acc, curr) => acc + curr,
        0,
      );

      if (currentTotal >= 100) {
        alert("주문 수량은 최대 100개까지 가능합니다.");
        return prev;
      }

      return { ...prev, [id]: (prev[id] ?? 0) + 1 };
    });
  };

  const removeFromCart = (id: string) => {
    setCart((prev) => {
      const current = prev[id] ?? 0;
      if (current <= 1) {
        // 1개 이하일 때는 장바구니에서 제거
        const next = { ...prev };
        delete next[id];
        return next;
      }
      // 그 외에는 수량만 1 감소
      return { ...prev, [id]: current - 1 };
    });
  };

  const totalCount = useMemo(
    () => Object.values(cart).reduce((acc, curr) => acc + curr, 0),
    [cart],
  );

  const totalPrice = useMemo(() => {
    return products.reduce((acc, product) => {
      const qty = cart[product.id] ?? 0;
      return acc + product.price * qty;
    }, 0);
  }, [products, cart]);

  const selectedItems = products.filter((product) => cart[product.id]);

  const isValidEmail = (email: string) => {
    // 매우 단순한 이메일 형식 검사
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const isValidPostcode = (postcode: string) => {
    // 숫자 5자리 검사
    return /^\d{5}$/.test(postcode);
  };

  const handleCheckout = async () => {
    if (selectedItems.length === 0) return;

    if (totalCount > 100) {
      alert("주문 수량은 최대 100개까지 가능합니다.");
      return;
    }

    if (!orderForm.email || !isValidEmail(orderForm.email)) {
      alert("유효한 이메일 주소를 입력해주세요.");
      return;
    }

    if (!orderForm.postalCode || !isValidPostcode(orderForm.postalCode)) {
      alert("우편번호는 숫자 5자리로 입력해주세요.");
      return;
    }

    setIsCheckoutLoading(true);
    try {
      const response = await fetch("/api/order", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: orderForm.email,
          address: orderForm.address,
          postcode: parseInt(orderForm.postalCode, 10),
          items: selectedItems.map((item) => ({
            menuId: Number(item.id),
            count: cart[item.id],
          })),
        }),
      });

      if (response.ok) {
        const data = await response.json();
        // 성공 응답은 빈 객체 {}
        alert("주문이 완료되었습니다");
        setCart({});
        setOrderForm({ email: "", address: "", postalCode: "" });
      } else {
        const error = await response.json();
        alert(error.message || "주문 처리 중 오류가 발생했습니다.");
      }
    } catch (err) {
      console.error(err);
      alert("주문 처리 중 오류가 발생했습니다.");
    } finally {
      setIsCheckoutLoading(false);
    }
  };

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setFormData({
      email: "",
      category: "",
      menu_name: "",
      price: "",
      image: "",
    });
  };

  const handleFormChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const { name, value } = e.target;

    if (name === "price") {
      const numeric = parseInt(value, 10);
      if (!Number.isNaN(numeric) && numeric > 10000000) {
        alert("품목 제안 금액은 최대 10,000,000원까지 가능합니다.");
        setFormData((prev) => ({ ...prev, [name]: "10000000" }));
        return;
      }
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    try {
      const response = await fetch("/api/menu", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: formData.email,
          category: formData.category,
          menu_name: formData.menu_name,
          price: parseInt(formData.price, 10),
          image: formData.image || "",
        }),
      });

      if (!response.ok) {
        throw new Error("상품 추가 제안에 실패했습니다");
      }

      const data = await response.json();
      alert(data.message || "상품 추가 제안이 접수되었습니다.");
      // 상품 목록을 즉시 최신 상태로 갱신
      setLoading(true);
      await loadProducts();
      handleCloseModal();
    } catch (err) {
      console.error(err);
      alert("상품 추가 제안 중 오류가 발생했습니다.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteMenu = async (product: Product) => {
    if (isDeleting) return;

    const email = window.prompt("삭제 권한 확인을 위해 이메일을 입력해주세요.");
    if (!email) return;

    if (!isValidEmail(email)) {
      alert("유효한 이메일 주소를 입력해주세요.");
      return;
    }

    setIsDeleting(true);
    try {
      const response = await fetch(`/api/menu/${product.id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        const data = await response.json().catch(() => ({}));
        alert(data.message || "메뉴가 삭제되었습니다.");
        setProducts((prev) => prev.filter((p) => p.id !== product.id));
      } else {
        const error = await response.json().catch(() => ({}));
        alert(error.message || "메뉴 삭제에 실패했습니다.");
      }
    } catch (err) {
      console.error(err);
      alert("메뉴 삭제 중 오류가 발생했습니다.");
    } finally {
      setIsDeleting(false);
    }
  };

  const handleOpenHistoryModal = () => {
    setIsHistoryModalOpen(true);
    setOrderHistory(null);
  };

  const handleCloseHistoryModal = () => {
    setIsHistoryModalOpen(false);
    setHistoryEmail("");
    setOrderHistory(null);
    setIsHistoryLoading(false);
  };

  const handleFetchOrderHistory = async () => {
    if (!historyEmail || !isValidEmail(historyEmail)) {
      alert("유효한 이메일 주소를 입력해주세요.");
      return;
    }

    setIsHistoryLoading(true);
    setOrderHistory(null);
    try {
      const response = await fetch("/api/order/history", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email: historyEmail }),
      });

      if (response.ok) {
        const data: OrderHistory = await response.json();
        setOrderHistory(data);
      } else if (response.status === 404) {
        const error = await response.json().catch(() => ({}));
        alert(error.message || "해당 이메일의 주문 내역이 없습니다.");
      } else {
        const error = await response.json().catch(() => ({}));
        alert(error.message || "주문 내역 조회에 실패했습니다.");
      }
    } catch (err) {
      console.error(err);
      alert("주문 내역 조회 중 오류가 발생했습니다.");
    } finally {
      setIsHistoryLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-start justify-center bg-slate-100 py-12">
      <main className="flex w-[1200px] max-w-[95vw] gap-6">
        <section className="flex-1 rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
          <div className="mb-3 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <h2 className="text-lg font-semibold text-slate-800">상품 목록</h2>
            </div>
            <button
              type="button"
              onClick={handleOpenModal}
              className="rounded-md border border-emerald-600 px-3 py-1 text-xs font-semibold text-emerald-700 transition hover:bg-emerald-600 hover:text-white whitespace-nowrap"
            >
              상품 추가 제안
            </button>
          </div>

          <div className="overflow-hidden rounded-lg border border-slate-200">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-slate-600">
                <tr>
                  <th className="w-20 px-4 py-3">이미지</th>
                  <th className="px-4 py-3">상품명</th>
                  <th className="w-28 px-4 py-3 text-right">가격</th>
                  <th className="w-32 px-4 py-3 text-center">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {loading ? (
                  <tr>
                    <td className="px-4 py-6 text-center" colSpan={4}>
                      상품을 불러오는 중입니다...
                    </td>
                  </tr>
                ) : (
                  products.map((product) => (
                    <tr key={product.id} className="hover:bg-slate-50/80">
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2">
                          <button
                            type="button"
                            className="flex h-6 w-6 items-center justify-center rounded-full border border-slate-300 bg-white text-slate-400 hover:border-rose-400 hover:text-rose-500"
                            title="메뉴 삭제 요청"
                            onClick={() => handleDeleteMenu(product)}
                            disabled={isDeleting}
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              className="h-3 w-3"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth="2"
                              strokeLinecap="round"
                              strokeLinejoin="round"
                            >
                              <polyline points="3 6 5 6 21 6" />
                              <path d="M19 6l-1 14H6L5 6" />
                              <path d="M10 11v6" />
                              <path d="M14 11v6" />
                              <path d="M9 6V4h6v2" />
                            </svg>
                          </button>
                          {product.img_url ? (
                            <img
                              src={product.img_url}
                              alt={product.name}
                              className="h-14 w-14 rounded-md border border-slate-200 object-cover shadow-inner"
                              onError={(e) => {
                                // 이미지 로드 실패 시 기본 div로 대체
                                const target = e.target as HTMLImageElement;
                                target.style.display = "none";
                              }}
                            />
                          ) : (
                            <div className="h-14 w-14 rounded-md border border-slate-200 bg-gradient-to-br from-slate-100 to-slate-200 shadow-inner" />
                          )}
                        </div>
                      </td>
                      <td className="px-4 py-3 align-top">
                        <div className="text-sm font-semibold text-slate-800">
                          {product.name}
                        </div>
                        {(product.subtitle || product.category) && (
                          <div className="text-xs text-slate-500">
                            {product.subtitle || product.category}
                          </div>
                        )}
                      </td>
                      <td className="px-4 py-3 text-right font-semibold text-slate-800">
                        {formatPrice(product.price)}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center justify-center gap-2 whitespace-nowrap">
                          <button
                            className="rounded-md border border-emerald-600 px-3 py-1 text-xs font-semibold text-emerald-700 transition hover:bg-emerald-600 hover:text-white whitespace-nowrap"
                            onClick={() => addToCart(product.id)}
                          >
                            추가
                          </button>
                          <button
                            className="rounded-md border border-rose-500 px-3 py-1 text-xs font-semibold text-rose-600 transition hover:bg-rose-500 hover:text-white whitespace-nowrap"
                            onClick={() => removeFromCart(product.id)}
                          >
                            삭제
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
                {!loading && products.length === 0 && (
                  <tr>
                    <td className="px-4 py-6 text-center text-slate-500" colSpan={4}>
                      표시할 상품이 없습니다.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>

        <section className="w-[360px] rounded-lg border border-slate-200 bg-slate-100/70 p-4 shadow-sm">
          <h2 className="mb-3 text-base font-semibold text-slate-800">Summary</h2>
          <div className="mb-3 space-y-2 rounded-md bg-white p-3 text-sm shadow-inner">
            {selectedItems.length === 0 ? (
              <p className="text-xs text-slate-500">선택된 상품이 없습니다.</p>
            ) : (
              selectedItems.map((item) => (
                <div key={item.id} className="flex items-center justify-between text-slate-700">
                  <div className="flex items-center gap-2">
                    <span className="h-2 w-2 rounded-full bg-slate-400" />
                    <span>{item.name}</span>
                  </div>
                  <span className="text-xs text-slate-500">x{cart[item.id]}</span>
                </div>
              ))
            )}
          </div>

          <div className="space-y-2 text-sm">
            <label className="block space-y-1">
              <span className="text-slate-600">이메일</span>
              <input
                type="email"
                value={orderForm.email}
                onChange={(e) =>
                  setOrderForm((prev) => ({ ...prev, email: e.target.value }))
                }
                className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                placeholder="your@email.com"
              />
            </label>
            <label className="block space-y-1">
              <span className="text-slate-600">주소</span>
              <input
                type="text"
                value={orderForm.address}
                onChange={(e) =>
                  setOrderForm((prev) => ({ ...prev, address: e.target.value }))
                }
                className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                placeholder="주소를 입력하세요"
              />
            </label>
            <label className="block space-y-1">
              <span className="text-slate-600">우편번호</span>
              <input
                type="text"
                value={orderForm.postalCode}
                onChange={(e) => {
                  // 숫자만 입력 허용
                  const value = e.target.value.replace(/\D/g, "");
                  setOrderForm((prev) => ({
                    ...prev,
                    postalCode: value,
                  }));
                }}
                maxLength={5}
                className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                placeholder="00000"
              />
            </label>
          </div>

          <p className="mt-4 text-xs text-slate-500">
            당일 오후 2시 이후의 주문은 다음 날 배송을 시작합니다.
          </p>

          <div className="mt-4 flex items-center justify-between rounded-md bg-white px-3 py-2 text-sm shadow-inner">
            <span className="text-slate-600">총 금액</span>
            <div className="text-right">
              <div className="text-lg font-bold text-slate-900">
                {formatPrice(totalPrice)}
              </div>
              <div className="text-xs text-slate-500">
                {totalCount}개 선택됨
              </div>
            </div>
          </div>

          <button
            onClick={handleCheckout}
            className="mt-4 w-full rounded-md bg-slate-900 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-400"
            disabled={selectedItems.length === 0 || isCheckoutLoading}
          >
            {isCheckoutLoading ? "처리 중..." : "결제하기"}
          </button>

          <button
            onClick={handleOpenHistoryModal}
            className="mt-2 w-full rounded-md border border-slate-300 bg-white py-2 text-xs font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            주문 내역 조회
          </button>

        </section>
      </main>

      {/* 상품 추가 제안 모달 */}
      {isModalOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
          onClick={handleCloseModal}
        >
          <div
            className="w-full max-w-md rounded-lg bg-white p-6 shadow-xl"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-xl font-semibold text-slate-800">
                상품 추가 제안
              </h3>
              <button
                onClick={handleCloseModal}
                className="text-slate-400 transition hover:text-slate-600"
                disabled={isSubmitting}
              >
                <svg
                  className="h-6 w-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <label className="block space-y-1">
                <span className="text-sm font-medium text-slate-700">
                  이메일
                </span>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleFormChange}
                  required
                  className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                  placeholder="your@email.com"
                />
              </label>

              <label className="block space-y-1">
                <span className="text-sm font-medium text-slate-700">분류</span>
                <input
                  type="text"
                  name="category"
                  value={formData.category}
                  onChange={handleFormChange}
                  required
                  className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                  placeholder="분류를 입력하세요 (예: 커피콩, 식품, 의류)"
                />
              </label>

              <label className="block space-y-1">
                <span className="text-sm font-medium text-slate-700">
                  제품명
                </span>
                <input
                  type="text"
                  name="menu_name"
                  value={formData.menu_name}
                  onChange={handleFormChange}
                  required
                  className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                  placeholder="제품명을 입력하세요"
                />
              </label>

              <label className="block space-y-1">
                <span className="text-sm font-medium text-slate-700">가격</span>
                <input
                  type="number"
                  name="price"
                  value={formData.price}
                  onChange={handleFormChange}
                  required
                  min="0"
                  max="10000000"
                  step="1"
                  className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                  placeholder="0"
                />
              </label>

              <label className="block space-y-1">
                <span className="text-sm font-medium text-slate-700">
                  이미지 (선택사항)
                </span>
                <input
                  type="url"
                  name="image"
                  value={formData.image}
                  onChange={handleFormChange}
                  className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                  placeholder="이미지 URL을 입력하세요"
                />
                <p className="text-xs text-slate-500">
                  이미지 URL을 입력하거나 파일을 업로드하세요
                </p>
              </label>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={handleCloseModal}
                  disabled={isSubmitting}
                  className="flex-1 rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  취소
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex-1 rounded-md bg-emerald-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isSubmitting ? "제출 중..." : "제안하기"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 주문 내역 조회 모달 */}
      {isHistoryModalOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
          onClick={handleCloseHistoryModal}
        >
          <div
            className="w-full max-w-lg rounded-lg bg-white p-6 shadow-xl"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-xl font-semibold text-slate-800">
                주문 내역 조회
              </h3>
              <button
                onClick={handleCloseHistoryModal}
                className="text-slate-400 transition hover:text-slate-600"
                disabled={isHistoryLoading}
              >
                <svg
                  className="h-6 w-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block space-y-1">
                  <span className="text-sm font-medium text-slate-700">
                    이메일
                  </span>
                  <input
                    type="email"
                    value={historyEmail}
                    onChange={(e) => setHistoryEmail(e.target.value)}
                    className="w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-slate-800 outline-none ring-emerald-500/60 transition focus:ring"
                    placeholder="주문 시 사용한 이메일을 입력하세요"
                    disabled={isHistoryLoading}
                  />
                </label>
                <button
                  type="button"
                  onClick={handleFetchOrderHistory}
                  disabled={isHistoryLoading}
                  className="mt-3 w-full rounded-md bg-emerald-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isHistoryLoading ? "조회 중..." : "주문 내역 조회"}
                </button>
              </div>

              {orderHistory && (
                <div className="mt-4 space-y-3 rounded-md border border-slate-200 bg-slate-50 p-3 text-sm">
                  <div className="mb-2 flex items-center justify-between">
                    <div>
                      <div className="font-semibold text-slate-800">
                        {orderHistory.email}
                      </div>
                      <div className="text-xs text-slate-500">
                        총 {orderHistory.orders.length}건의 주문
                      </div>
                    </div>
                  </div>

                  <div className="max-h-64 space-y-3 overflow-y-auto">
                    {orderHistory.orders.map((order, orderIdx) => (
                      <div
                        key={orderIdx}
                        className="rounded-md border border-slate-200 bg-white"
                      >
                        <div className="flex items-center justify-between border-b border-slate-100 px-3 py-2 text-xs text-slate-600">
                          <span>
                            배송지: {order.address}{" "}
                            {order.postcode ? `(${order.postcode})` : ""}
                          </span>
                          <span>주문 #{orderIdx + 1}</span>
                        </div>
                        <table className="w-full text-left text-xs">
                          <thead className="bg-slate-100 text-slate-600">
                            <tr>
                              <th className="px-3 py-2">메뉴명</th>
                              <th className="w-20 px-3 py-2 text-right">
                                가격
                              </th>
                              <th className="w-16 px-3 py-2 text-center">
                                수량
                              </th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-slate-100">
                            {(order.items ?? []).map((item, idx) => (
                              <tr key={idx}>
                                <td className="px-3 py-2">{item.menuName}</td>
                                <td className="px-3 py-2 text-right">
                                  {formatPrice(item.menuPrice)}
                                </td>
                                <td className="px-3 py-2 text-center">
                                  x{item.count}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
