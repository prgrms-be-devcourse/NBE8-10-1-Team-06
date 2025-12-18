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

type MenuListResponse = {
  menu_id: number;
  category: string;
  menu_name: string;
  price: number;
  img_url: string;
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

  useEffect(() => {
    const load = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/menu", {
          cache: "no-store",
        });

        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }

        const data: MenuListResponse[] = await res.json();

        const convertedProducts: Product[] = data.map((item) => ({
          id: item.menu_id.toString(),
          name: item.menu_name,
          category: item.category,
          price: item.price,
          img_url: item.img_url,
        }));

        setProducts(convertedProducts);
      } catch (err) {
        console.error("백엔드 API 호출 실패:", err);
        setProducts([]);
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  const addToCart = (id: string) => {
    setCart((prev) => ({ ...prev, [id]: (prev[id] ?? 0) + 1 }));
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
          items: selectedItems.map((item) => ({
            menu_id: Number(item.id),
            menu_count: cart[item.id],
          })),
          customer: {
            email: orderForm.email,
            postcode: parseInt(orderForm.postalCode, 10),
            address: orderForm.address,
          },
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
        }),
      });

      if (!response.ok) {
        throw new Error("상품 추가 제안에 실패했습니다");
      }

      const data = await response.json();
      alert(data.message || "상품 추가 제안이 접수되었습니다.");
      handleCloseModal();
    } catch (err) {
      console.error(err);
      alert("상품 추가 제안 중 오류가 발생했습니다.");
    } finally {
      setIsSubmitting(false);
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
                        ) : null}
                        {!product.img_url && (
                          <div className="h-14 w-14 rounded-md border border-slate-200 bg-gradient-to-br from-slate-100 to-slate-200 shadow-inner" />
                        )}
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
    </div>
  );
}