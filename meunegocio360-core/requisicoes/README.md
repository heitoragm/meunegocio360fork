# Requisições manuais (REST Client)

Arquivos `.http` para verificar manualmente os critérios de aceite dos requisitos, até a fase de testes automatizados ([RNF-016](../../docs/requisitos-gerais.md#33-dependências)). Use a extensão **REST Client** (`humao.rest-client`) do VS Code: abra um arquivo e clique em **Send Request**, acima de cada requisição.

| Arquivo | Cenários de RF-BE-001 |
| --- | --- |
| `01-fluxo-de-sessao.http` | Login, `/me`, renovação, logout e refresh recusado após logout. |
| `02-erros-e-seguranca.http` | Credenciais inválidas, e-mail inexistente, validação, tokens inválidos, *deny-by-default*, `Origin` não permitida. |
| `03-reuso-de-refresh-token.http` | Detecção de reuso e revogação da família. |
| `04-bloqueio-por-tentativas.http` | Bloqueio após 5 falhas, `429` com `Retry-After` e desbloqueio. |
| `05-empresa-admin.http` | RF-BE-002 como `ADMIN`: primeiro cadastro, CNPJ inválido, edição com `versao`, conflito de versão, inativação e reativação. |
| `06-empresa-operador-comercial.http` | RF-BE-002 como `OPERADOR_COMERCIAL`: consulta permitida; cadastro, edição e situação negados (`403`). |
| `07-empresa-operador-financeiro.http` | RF-BE-002 como `OPERADOR_FINANCEIRO`: consulta permitida; escrita negada (`403`). |
| `08-clientes-admin.http` | RF-BE-003 como `ADMIN`: cadastro de pessoa jurídica, documento incompatível, busca por documento, edição com `versao`, conflito de versão e exclusão. |
| `09-clientes-operador-comercial.http` | RF-BE-003 como `OPERADOR_COMERCIAL`: CPF com máscara, documento duplicado, busca sem acentos, inativação, filtro `situacao` e reativação. |
| `10-clientes-operador-financeiro.http` | RF-BE-003 como `OPERADOR_FINANCEIRO`: tudo responde `403`, mesmo com corpo inválido. |
| `11-fornecedores-admin.http` | RF-BE-004 como `ADMIN`: mesmo CNPJ como cliente e como fornecedor, documento duplicado, busca sem acentos, exclusão e `SUPPLIER_NOT_FOUND`. |
| `12-fornecedores-operador-comercial.http` | RF-BE-004 como `OPERADOR_COMERCIAL`: tudo responde `403` (matriz invertida em relação a clientes), mesmo com corpo inválido. |
| `13-fornecedores-operador-financeiro.http` | RF-BE-004 como `OPERADOR_FINANCEIRO`: CNPJ com máscara, CNPJ inválido, busca por documento, edição com `versao`, conflito, inativação e exclusão. |
| `14-catalogo-admin.http` | RF-BE-005 como `ADMIN`: categoria duplicada sem acento, código duplicado, `tipo` imutável, categoria em uso, inativação de categoria e exclusões. |
| `15-catalogo-operador-comercial.http` | RF-BE-005 como `OPERADOR_COMERCIAL`: produto (`UN`) e serviço (`H`), preço negativo ou com vírgula, filtros por tipo e categoria, busca por código, edição com `versao` e conflito. |
| `16-catalogo-operador-financeiro.http` | RF-BE-005 como `OPERADOR_FINANCEIRO`: itens e categorias respondem `403`, mesmo com corpo inválido. |

## Preparação

1. No `.env` da raiz:
   - `JWT_SECRET` com pelo menos 32 bytes (`openssl rand -base64 48`). Sem isso a aplicação não sobe;
   - `SEED_ADMIN_SENHA`, `SEED_USUARIO_SENHA` e `SEED_FINANCEIRO_SENHA` com 8 a 72 caracteres. São as senhas de `admin@meunegocio360.com.br` (`ADMIN`), `usuario@meunegocio360.com.br` (`OPERADOR_COMERCIAL`) e `financeiro@meunegocio360.com.br` (`OPERADOR_FINANCEIRO`), criados apenas no perfil `dev`;
   - `APP_AUTH_COOKIE_SECURE=false`. O REST Client não reenvia cookies `Secure` em `http://`, e o requisito permite esse valor somente no perfil `dev`;
   - opcional: `APP_AUTH_BLOQUEIO_MINUTOS=1`, para testar o desbloqueio sem esperar 15 minutos.
2. `docker compose up -d` na raiz e `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` em `meunegocio360-core`.
3. Confira que a configuração `rest-client.rememberCookiesForSubsequentRequests` está ativa (padrão `true`). É ela que guarda o cookie `mn360_refresh` entre as requisições, como faria o navegador.

As senhas são pedidas na hora de enviar (`# @prompt`) e nunca ficam gravadas nos arquivos ([RNF-022](../../docs/requisitos-gerais.md#34-segurança-e-privacidade)).

## Observações

- Requisições nomeadas (`# @name`) só podem ser referenciadas dentro do mesmo arquivo. Por isso, cada arquivo faz o próprio login.
- O cookie guardado vale para `localhost`. Quando é preciso enviar um cookie escolhido à mão (reuso, token desconhecido), a requisição usa `127.0.0.1`. Assim o REST Client não troca o cabeçalho `Cookie` pelo que guardou.
- Para inspecionar o banco: `docker exec -it meunegocio360-core-db mysql -u meunegocio360 -p meunegocio360_core` e, por exemplo, `SELECT HEX(familia_id), token_hash, revogado_em FROM refresh_token;`. Só o hash SHA-256 é gravado.
