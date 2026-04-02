## 문서 추가·삭제 시 네비게이션 동기화

`docs/` 하위에 파일을 **추가하거나 삭제**하면 `mkdocs.yml`의 `nav:` 섹션을 반드시 함께 갱신한다.

기존 파일을 **수정만** 하는 경우에는 갱신 불필요.

## 새 문서 작성 시 Breadcrumb 필수

`docs/` 하위에 파일을 새로 작성할 때 **파일 최상단 첫 줄**에 breadcrumb 네비게이션을 반드시 추가한다.

```
> [← 홈](/index.md) · [Java](/java/java.md) · [섹션명](/java/섹션/섹션.md)
```

계층에 따라 적절히 조정한다:
- 최상위 섹션 인덱스: `> [← 홈](/index.md)`
- 섹션 인덱스: `> [← 홈](/index.md) · [Java](/java/java.md)`
- 하위 파일: `> [← 홈](/index.md) · [Java](/java/java.md) · [섹션명](/java/섹션/섹션.md)`

## GitHub Pages 배포 구조

| 브랜치 | 역할 | 관리 주체 |
|--------|------|-----------|
| `main` | 소스 (마크다운, 설정 파일) | 직접 작성 |
| `gh-pages` | 빌드 결과물 (HTML) | GitHub Actions 자동 생성 |

- GitHub Pages 소스: `gh-pages` 브랜치 루트
- 빌드 트리거: `main` 브랜치 push → GitHub Actions → `mkdocs gh-deploy`
- `gh-pages` 브랜치는 직접 수정하지 않는다 (Actions가 덮어씀)

## GitHub Pages 관련 작업 시 체크리스트

사이트 설정 변경(`mkdocs.yml`) 또는 테마·플러그인 변경 시:
1. 변경사항 `main`에 push
2. Actions 탭에서 빌드 성공 확인 (`https://github.com/KROOKIMWOOBIN/study/actions`)
3. `gh-pages` 브랜치 업데이트 확인 후 사이트 검증
